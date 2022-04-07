package com.yingenus.pocketchinese.functions.search

import android.content.Context
import android.util.Log
import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.domain.dto.UnitWord
import com.yingenus.pocketchinese.domain.dto.VariantWord
import kotlinx.serialization.PrimitiveKind
import java.io.File
import java.io.OutputStream
import java.nio.charset.Charset
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

class IndexManagerImpl() : IndexManager {

    private data class WordLink(val word : String, val data : List<VariantWordLink>)
    private class VariantWordLink(val byteArray: ByteArray)

    companion object{
        private const val extension : String = "index"
        private const val index_directory : String= "indexes"
        private val path :  (context : Context) -> String = { context ->  context.filesDir.absolutePath + index_directory }
        private val fileName : (language: Language, version : Int) -> String = { language, version ->  "${langSing(language)}_${version}.${extension}"}

        private val langSing : (language: Language) -> String = { language ->
            when (language){
                Language.CHINESE -> "chin"
                Language.RUSSIAN -> "rus"
                Language.PINYIN -> "pin"
            }
        }

        private val version : (name : String) -> Int = {
            val version = it.subSequence(it.indexOf("_")+1, it.lastIndexOf(".")).toString()
            version.toIntOrNull()?: -1
        }

        private val charset : (language: Language) -> Charset = { getCharset(it) }

        private fun getIterator( repository: UnitWordRepository) : UnitWordIterator = UnitWordIterator(repository)

        private fun UnitWord.toWordLink(): WordLink{

            val variantsLink = this.mentions.map { it.toVariantWordLink() }

            return WordLink(this.word,variantsLink)
        }

        private fun VariantWord.toVariantWordLink(): VariantWordLink{
            val idPlus_2:Int = this.id shl 10
            val idPlus:Int = idPlus_2 + this.index;
            val weight:Short = this.weight.toShort();

            val array : ByteArray = toByteArray(idPlus) + toByteArray(weight, size = 2);

            return VariantWordLink(array)
        }

        private fun toByteArray(data : Number,size : Int = 4): ByteArray =
                ByteArray(size){index -> (data.toLong() shr (index * 8)).toByte()}.reversedArray()
        private fun toCharArray(data : Number,size : Int = 4): CharArray =
                CharArray(size){index -> (data.toLong() shr (index * 8)).toChar()}
    }

    override fun supportLanguage(): List<Language> {
        return  prefixLanguages
    }

    override fun checkIndex(language: Language, version : Int, context: Context): Boolean {
        require(prefixLanguages.contains(language), {"Not support language : ${language::name}"})
        initPathIfNotExist(path(context))

        val files = File(path(context)).listFiles()?: emptyArray()
        return files.any { file -> file.name == fileName(language, version) }
    }

    override fun getIndexAbsolutePath(language: Language, version : Int, context: Context): String {
        require(prefixLanguages.contains(language), {"Not support language : ${language::name}"})
        initPathIfNotExist(path(context))

        val file = (File(path(context)).listFiles()?: emptyArray()).find { it.name == fileName(language, version) }

        file ?: throw NoSuchFieldException(" cant find file for language : ${language.name} and version : $version")

        return file.absolutePath
    }

    override fun createIndex(
        language: Language,
        version : Int,
        unitWordRepository: UnitWordRepository,
        context: Context
    ) {
        require(prefixLanguages.contains(language), {"Not support language : ${language::name}"})
        initPathIfNotExist(path(context))

        //delete another file for this language
        val files = (File(path(context)).listFiles()?: emptyArray()).filter { it.name.contains(
            langSing(language)) }

        files.forEach { file -> file.delete() }

        //crete new index file

        val newIndexFile = File(path(context)+"/"+ fileName(language, version))

        newIndexFile.createNewFile()

        createIndex(newIndexFile, charset(language), getIterator(unitWordRepository))

    }

    override fun getIndexAbsolutePathLast(language: Language, context: Context): String {
        require(prefixLanguages.contains(language), {"Not support language : ${language::name}"})
        initPathIfNotExist(path(context))

        val files = (File(path(context)).listFiles()?: emptyArray()).filter { it.name.contains(
            langSing(language)) }.map { version(it.name) to it }

        val versions = files.map { it.first }

        val maxVersion = versions.maxOrNull()

        maxVersion?: throw NoSuchFieldException(" cant find file for language : ${language.name} and version : $version")

        return files.find { it.first == maxVersion }?.second?.absolutePath!!
    }

    private fun initPathIfNotExist(absolutPath : String){
        val folder = File(absolutPath)
        if(!folder.exists()){
            folder.mkdirs()
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun createIndex(file: File, charset: Charset, iterator : UnitWordIterator) {

        val time = measureTimedValue {
            require(file.isFile)
            require(file.canWrite())

            val out = file.outputStream()
            val indexW = IndexedOutPut(out);

            iterator.forEach { it ->

                val word = it.toWordLink()

                //write word length to the file 2 byte
                indexW.write(toByteArray(word.word.length, 2))
                //write word
                //word.word.format()
                indexW.write(word.word.toByteArray(charset))
                val startIndex = indexW.position + 8
                //write variants words data
                indexW.write(toByteArray(startIndex, 4))
                //write length of variants word
                indexW.write(toByteArray(word.data.size * 6))
                //write variants word
                word.data.forEach {
                    indexW.write(it.byteArray)
                }
            }

            indexW.flush()
            indexW.close()
            out.close()
        }
        Log.d("IndexCreatorImpl"," The init operation took: $time ms")
    }


    class IndexedOutPut (val ops : OutputStream) : OutputStream(){

        var position : Int =0
            private set

        override fun close() {
            ops.close()
        }

        override fun flush() {
            ops.flush()
        }

        override fun write(b: Int) {
            write(ByteArray(4){index -> (b.toLong() shr (index * 8)).toByte()})
        }

        override fun write(b: ByteArray) {
            write(b,0,b.size)
        }

        override fun write(b: ByteArray, off: Int, len: Int) {
            val subBuf : ByteArray = b.copyOfRange(off,len)
            position += subBuf.size
            ops.write(subBuf,0,subBuf.size)
        }
    }

}
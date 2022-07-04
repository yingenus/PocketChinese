package com.yingenus.pocketchinese.functions.search

import android.content.Context
import android.util.Log
import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.domain.dto.UnitWord
import com.yingenus.pocketchinese.domain.dto.VariantWord
import com.yingenus.pocketchinese.functions.search.indexfile.IndexChecker
import com.yingenus.pocketchinese.functions.search.indexfile.IndexCreator
import kotlinx.serialization.PrimitiveKind
import java.io.File
import java.io.OutputStream
import java.nio.charset.Charset
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

class IndexManagerImpl(
    private val indexCreator: IndexCreator,
    private val indexChecker: IndexChecker
    ) : IndexManager {

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

    }

    override fun supportLanguage(): List<Language> {
        return  prefixLanguages
    }

    override fun checkIndex(language: Language, version : Int, context: Context): Boolean {
        require(prefixLanguages.contains(language), {"Not support language : ${language::name}"})
        initPathIfNotExist(path(context))

        val files = File(path(context)).listFiles()?: emptyArray()

        val expectedName = fileName(language, version)

        return if ( files.any { file -> file.name == fileName(language, version) } ){
            val indexFile =File(path(context)+"/"+expectedName)
            if (!indexFile.exists()) false
            else indexChecker.checkIndexFile(indexFile)
        }else{
            false
        }
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

        indexCreator.createIndex(newIndexFile, charset(language), getIterator(unitWordRepository))
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

}
package com.yingenus.pocketchinese.functions.search

import android.content.Context
import com.yingenus.pocketchinese.common.Language
import java.io.File
import java.lang.IllegalArgumentException
import java.lang.RuntimeException

class CreatePrefixSearchIteratorImpl(
    private val unitWordsVersion : Int,
    vararg wordRepositorys: Pair<Language, UnitWordRepository>
    ) : CreateNativeSearcherIterator{

    private val reposytorys : List<Pair<Language,UnitWordRepository>>

    init {
        reposytorys = wordRepositorys.toList()
    }

    private companion object{
        const val extension : String = ".index"
        const val index_directory : String= "indexes"

        // rus_verison.index

        fun name(lang : Language) =
            when (lang){
                Language.CHINESE -> "chin"
                Language.RUSSIAN -> "rus"
                Language.PINYIN -> "pin"
            }

        fun getFileName(lang: Language, version: Int) = "${name(lang)}_${version}${extension}"

    }

    override fun createNativeSearcher(language: Language, context: Context): NativeSearcher {
        val dbVersion : Int = unitWordsVersion

        val path : String = context.filesDir.absolutePath + "/" + index_directory

        checkPath(path)

        val oldIndex = getOldIndex(path,language)

        val indexAbsolutName : String

        if (oldIndex == null || oldIndex.first < dbVersion){
            indexAbsolutName = path+"/"+getFileName(language,dbVersion)
            createIndex(indexAbsolutName, language)
        }
        else{
            indexAbsolutName = oldIndex.second.absolutePath
        }

        val searcher : PrefixSearcher = PrefixSearcher()

        searcher.setLanguage(language)
        searcher.init(indexAbsolutName)


        return NativeSearcher(searcher, getCharset(language))
    }

    private fun checkPath(absolutPath : String){
        val folder = File(absolutPath)
        if(!folder.exists()){
            folder.mkdirs()
        }
    }

    private fun createIndex(absolutName : String, lang: Language){

        val iterator = UnitWordIterator(getRepository(lang))

        val indexCreator = IndexCreatorImpl.getIndexCreator(iterator, lang)

        val file = File(absolutName)

        if (!file.exists()){
            file.createNewFile()
        }

        indexCreator.createIndex(File(absolutName))
    }

    private fun getRepository(lang: Language): UnitWordRepository{
        return reposytorys.find { it.first == lang }?.second
            ?: throw IllegalArgumentException("cant find such language :${lang::name}")
    }

    private fun getOldIndex(path : String, lang: Language): Pair<Int,File>?{

        val langCode = name(lang)

        val files = File(path).listFiles()?: emptyArray()
        val fileByVersion : List<Pair<Int, File>> = files.filter { it.name.contains(name(lang)) }
            .filterNotNull()
            .map {
                getVersion(it.name) to it
            }

        var max : Pair<Int,File>? = null

        fileByVersion.forEach {
            if (max == null) max = it
            else if (max!!.first > it.first) max = it
        }

        return max
    }

    private fun getVersion(fileName : String): Int{
        val version = fileName.substring(fileName.indexOf("_")+1,fileName.indexOf("."))

        return version.toIntOrNull()?: throw RuntimeException("cant get version of index in :${fileName}")
    }



}
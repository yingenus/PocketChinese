package com.yingenus.pocketchinese.domain.entitiys.dictionary

import android.content.Context
import android.content.res.AssetManager
import com.yingenus.pocketchinese.domain.repository.ChinCharRepository
import java.lang.RuntimeException

class DictionarySearch {

    enum class SearchFormat{
        FUZZY,MATCH
    }

    private companion object{
        const val resDir = "dictres"
        const val rusfuzz = "russianWords.res"
        const val chnfuzz1 = "n1chinese.res"
        const val chnfuzz2 = "n2chinese.res"
        const val pinfuzz = "pinyinWords.res"
        const val rus = "russian.res"
        const val pin = "pinyin.res"
        const val chn = "chinese.res"
    }


    //private lateinit var chinDao : ChinCharDaoImpl
    private var chinCharRepository : ChinCharRepository?= null

    private var resources : Context? = null

    private var rusEngineRef : SearchEngine? = null
        get() {
            if (field == null){
                field = getSearchEngine(RusBuilder::build, listOf(rus))
            }
            return field
        }
    private var pinEngineRef : SearchEngine? = null
        get() {
            if (field == null ){
                field = getSearchEngine(PinBuilder::build, listOf(pin))
            }
            return field
        }
    private var chnEngineRef : SearchEngine? = null
        get() {
            if (field == null){
                field = getSearchEngine (ChnBuilder::build, listOf(chn) )
            }
            return field
        }
    private var fuzzyRusEngineRef  : SearchEngine? = null
        get() {
            if (field == null){
                field = getSearchEngine(FuzzyRusBuilder::build, listOf(rusfuzz))
            }
            return field
        }
    private var fuzzyPinEngineRef  : SearchEngine? = null
        get() {
            if (field == null){
                field = getSearchEngine(FuzzyPinBuilder::build, listOf(pinfuzz))
            }
            return field
        }
    private var fuzzyChnEngineRef : SearchEngine? = null
        get() {
            if (field == null){
                field = getSearchEngine(FuzzyChnBuilder::build, listOf(chnfuzz1, chnfuzz2))
            }
            return field
        }

    private val rusEngine: SearchEngine
        get() = if (searchType == SearchFormat.FUZZY)
            fuzzyRusEngineRef!! else rusEngineRef!!

    private val pinEngine: SearchEngine
        get() = if (searchType == SearchFormat.FUZZY)
            fuzzyPinEngineRef!! else pinEngineRef!!

    private val chnEngine: SearchEngine
        get() = if (searchType == SearchFormat.FUZZY)
            fuzzyChnEngineRef!! else chnEngineRef!!

    var searchType : SearchFormat = SearchFormat.FUZZY


    fun initDictionary(chinCharRepository: ChinCharRepository, resources: Context){
        //chinDao = dbDaoImpl
        this.chinCharRepository = chinCharRepository
        this.resources = resources
    }

    fun search(query : String) : List<com.yingenus.pocketchinese.domain.dto.ChinChar>{
        return when(language(query)){
            Language.RUSSIAN -> rusEngine.startSearch(query)
            Language.PINYIN -> pinEngine.startSearch(query)
            Language.CHINESE -> chnEngine.startSearch(query)
        }
    }

    fun close(){
        resources = null
    }

    fun whichLanguage(str:String):Language{
        return language(str)
    }

    private fun language(value: String)= when {
        value.contains(Regex("""[\p{script=Han}]""")) -> Language.CHINESE
        value.contains(Regex("""[А-яа-я]""")) -> Language.RUSSIAN
        else -> Language.PINYIN
    }

    private fun getSearchEngine(f : (ChinCharRepository, AssetManager, List<String> ) -> SearchEngine, fileNames : List<String>) : SearchEngine{
        resources ?: throw RuntimeException("no access to resources")
        if (chinCharRepository == null) throw RuntimeException("class should be initialized firstly ")

        return f(chinCharRepository!!,resources!!.assets, fileNames.map { resDir+"/"+it })
    }

}
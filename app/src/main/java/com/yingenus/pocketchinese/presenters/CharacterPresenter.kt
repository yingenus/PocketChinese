package com.yingenus.pocketchinese.presenters

import android.content.Context
import com.yingenus.pocketchinese.controller.dialog.CharacterInterface
import com.yingenus.pocketchinese.model.database.DictionaryDBOpenManger
import com.yingenus.pocketchinese.model.database.ExamplesDBOpenManger
import com.yingenus.pocketchinese.model.database.dictionaryDB.*

class CharacterPresenter(val view : CharacterInterface, private val chinId : Int) {

    private companion object{
        val isExamplesEnabled = true
        val maxExampsLength = 20
    }

    private lateinit var chiDaoImpl: ChinCharDaoImpl
    private lateinit var exampleDaoImpl: ExampleDaoImpl
    private lateinit var linksDaoImpl: LinksDaoImpl

    private lateinit var showChinChar: ChinChar

    private lateinit var redirectedChinChar: ChinChar

    fun onCreate(context: Context){
        val dictionaryConnection = DictionaryDBOpenManger.getHelper(context, DictionaryDBHelper::class.java).connectionSource
        val examplesConnection = ExamplesDBOpenManger.getHelper(context,ExamplesDBHelper::class.java).connectionSource


        chiDaoImpl = ChinCharDaoImpl(dictionaryConnection)
        exampleDaoImpl = ExampleDaoImpl(examplesConnection)
        linksDaoImpl = LinksDaoImpl(examplesConnection)

        showChinChar = getShowedChar()

        initViewHeader()
        initTranslations()
        initExamples()
        initEntryChins()

    }

    fun addCardClicked(){
        view.startAddNewStudy(showChinChar)
    }

    fun onDestroy(){
        DictionaryDBOpenManger.releaseHelper()
        ExamplesDBOpenManger.releaseHelper()
    }

    private fun getShowedChar(): ChinChar{
        var provided = chiDaoImpl.queryForId(chinId.toString())

        val translations = provided.translations

        val linked = translations.find { it.contains("\$link") }

        if (linked != null){
            if (!translations.filterNot { it.contains("\$link") }.any {
                        it.contains(Regex("""[А-яа-я]""")) }
            ) {
                redirectedChinChar = provided
                provided = chiDaoImpl.findChinCharInColumn(
                        linked.substring(linked.indexOf("{") + 1, linked.indexOf("}")), ChinChar.WORD_FIELD_NAME)
                        .firstOrNull() ?: provided
            }
        }
        return  provided
    }

    private fun initViewHeader(){
        val links = findLinked()

        if (links.isNotEmpty()){
            view.setLinked(links.map { it.chinese })
        }

        val tag = showChinChar.generalTag
        if (tag.isNotEmpty()){
            view.setTags(listOf(tag))
        }

        view.setChin( showChinChar.chinese )
        view.setPinyin( showChinChar.pinyin )
    }

    private fun initTranslations(){

        val result = mutableListOf<String>()

        val tags = showChinChar.specialTags
        val translations = showChinChar.translations

        for (i in translations.indices){

            if (translations[i].contains("\$link") || !translations[i].contains(Regex("""[А-Яа-я]""")))
                continue

            val line = (tags[i]?:"".toUpperCase())+" "+translations[i].capitalize()
            result.add(line)
        }

        view.setTranslations(result)
    }

    private fun initExamples(){
        if (isExamplesEnabled){
            val links = linksDaoImpl.findChinCharInColumn(showChinChar.id.toString(),Links.WORD_ID_FIELD_NAME).toList()

            val examples = mutableListOf<Example>()

            var count = 1
            loop@ for (link in links){
                for (id in link.examplIds){
                    val example = exampleDaoImpl.queryForId(id.toString())
                    examples.add(example)
                    if (count> maxExampsLength)
                        break@loop
                    count++
                }
            }

            if (examples.isNotEmpty()){
                view.setExamples(examples)
            }

        }
    }

    private fun initEntryChins(){
        if (showChinChar.chinese.length > 1){
            val entries = mutableListOf<ChinChar>()

            for (char in showChinChar.chinese.toList().map { it.toString() }){
                val chins =
                        chiDaoImpl.findChinCharInColumn(char,ChinChar.WORD_FIELD_NAME)

                for (chin in chins) {
                    if (showChinChar.chinese.contains(chin.chinese)
                            && showChinChar.pinyin.contains(chin.pinyin)){
                        entries.add(chin)
                        break
                    }
                }
            }

            if (entries.isNotEmpty()){
                view.setCharacters(entries)
                return
            }
        }
    }

    private fun findLinked(): List<ChinChar>{
        val links = showChinChar.translations.filterIndexed { _, s -> s.contains("\$link")  }

        val result = mutableMapOf<Int,ChinChar>()

        if (links.isNotEmpty()){
            for (link in links){
                val char = link.substring(link.indexOf("{")+1, link.indexOf("}"))

                val chins = chiDaoImpl.findChinCharInColumn(char, ChinChar.WORD_FIELD_NAME)

                chins.forEach { result[it.id] = it }
            }
        }

        if (::redirectedChinChar.isInitialized)
            result[redirectedChinChar.id] = redirectedChinChar

        return result.values.toList()
    }

}
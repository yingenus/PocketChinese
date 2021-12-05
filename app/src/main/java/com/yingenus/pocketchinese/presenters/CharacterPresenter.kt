package com.yingenus.pocketchinese.presenters

import android.content.Context
import android.util.Log
import com.yingenus.pocketchinese.controller.dialog.CharacterInterface
import com.yingenus.pocketchinese.domain.dto.ChinChar
import com.yingenus.pocketchinese.domain.repository.ChinCharRepository
import com.yingenus.pocketchinese.domain.repository.ExampleRepository
import com.yingenus.pocketchinese.domain.repository.ToneRepository
import com.yingenus.pocketchinese.logErrorMes
import com.yingenus.pocketchinese.model.database.DictionaryDBOpenManger
import com.yingenus.pocketchinese.model.database.ExamplesDBOpenManger
import com.yingenus.pocketchinese.model.database.dictionaryDB.*
import com.yingenus.pocketchinese.model.pinplayer.PinPlayer
import com.yingenus.pocketchinese.model.pinplayer.ToneSplitter
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject

class CharacterPresenter(val view : CharacterInterface, private val chinId : Int, private val chinCharRepository: ChinCharRepository, val exampleRepository: ExampleRepository, val toneRepository: ToneRepository) {

    private companion object{
        val isExamplesEnabled = true
        val maxExampsLength = 20
    }

    private lateinit var showChinChar: ChinChar

    private lateinit var redirectedChinChar: ChinChar

    private lateinit var makeSoundClicked : PublishSubject<String>
    private var pinPlayer : PinPlayer? = null

    fun onCreate(){

        showChinChar = getShowedChar()?: ChinChar(Int.MAX_VALUE, "-","-", emptyArray(),"-", emptyArray())

        makeSoundClicked = PublishSubject.create()

        initViewHeader()
        initTranslations()
        initExamples()
        initEntryChins()

    }

    fun addCardClicked(){
        view.startAddNewStudy(showChinChar)
    }

    fun makeSoundClicked(){
        makeSoundClicked.onNext(showChinChar.pinyin)
    }

    fun onResume(context: Context){
        pinPlayer = PinPlayer()
        val observer = makeSoundClicked
                .observeOn(Schedulers.computation())
                .filter { !pinPlayer!!.isPlaying() }
                .flatMap { ToneSplitter.rxSplitter(it, toneRepository) }
        try {
            pinPlayer!!.registerObserver(observer!!,context)
        }catch (e : Exception){
            Log.e("CharacterPresenter", e.logErrorMes())
        }

    }

    fun onPause(){
        pinPlayer?.release()
    }

    fun onDestroy(){

    }

    private fun getShowedChar(): ChinChar?{
        var provided = chinCharRepository.findById(chinId)

        val translations = provided!!.translation

        val linked = translations.find { it.contains("\$link") }

        if (linked != null){
            if (!translations.filterNot { it.contains("\$link") }.any {
                        it.contains(Regex("""[А-яа-я]""")) }
            ) {
                redirectedChinChar = provided
                provided = chinCharRepository.findByChinese(linked.substring(linked.indexOf("{") + 1, linked.indexOf("}"))).firstOrNull()
            }
        }
        return  provided
    }

    private fun initViewHeader(){
        val links = findLinked()

        if (showChinChar.chineseOld != null){
            view.setLinked(listOf(showChinChar.chineseOld!!))
        }
        else if (links.isNotEmpty()){
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

        val tags = showChinChar.specialTag
        val translations = showChinChar.translation

        for (i in translations.indices){

            if (translations[i].contains("\$link") /*|| !translations[i].contains(Regex("""[А-Яа-я]"""))*/)
                continue

            val line = (tags[i].toUpperCase())+" "+translations[i].capitalize()
            result.add(line)
        }

        view.setTranslations(result)
    }

    private fun initExamples(){
        if (isExamplesEnabled){
            val matchExamples = exampleRepository.fundByChinCharId(chinId)
            val examples = matchExamples.subList(0,if (matchExamples.size > maxExampsLength) maxExampsLength else matchExamples.lastIndex + 1)

            if (examples.isNotEmpty()){
                view.setExamples(examples)
            }

        }
    }

    private fun initEntryChins(){
        if (showChinChar.chinese.length > 1){
            val entries = mutableListOf<ChinChar>()

            for (char in showChinChar.chinese.toList().map { it.toString() }){
                val chins = chinCharRepository.findByChinese(char)

                for (chin in chins) {
                    if (chin.chinese.trim() == char
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
        val links = showChinChar.translation.filterIndexed { _, s -> s.contains("\$link")  }

        val result = mutableMapOf<Int,ChinChar>()

        if (links.isNotEmpty()){
            for (link in links){
                val char = link.substring(link.indexOf("{")+1, link.indexOf("}"))

                val chins = chinCharRepository.findByChinese(char)

                chins.forEach { result[it.id] = it }
            }
        }

        if (::redirectedChinChar.isInitialized)
            result[redirectedChinChar.id] = redirectedChinChar

        return result.values.toList()
    }

}
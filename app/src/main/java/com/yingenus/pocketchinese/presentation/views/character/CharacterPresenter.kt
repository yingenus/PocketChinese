package com.yingenus.pocketchinese.presentation.views.character

import android.content.Context
import android.util.Log
import com.yingenus.pocketchinese.domain.dto.DictionaryItem
import com.yingenus.pocketchinese.domain.repository.DictionaryItemRepository
import com.yingenus.pocketchinese.domain.repository.ExampleRepository
import com.yingenus.pocketchinese.domain.repository.ToneRepository
import com.yingenus.pocketchinese.logErrorMes
import com.yingenus.pocketchinese.domain.entitiys.pinplayer.PinPlayer
import com.yingenus.pocketchinese.domain.entitiys.pinplayer.ToneSplitter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.*

class CharacterPresenter @AssistedInject constructor(
        @Assisted val view : CharacterInterface,
        @Assisted private val chinId : Int,
        private val dictionaryItemRepository: DictionaryItemRepository,
        val exampleRepository: ExampleRepository,
        val toneRepository: ToneRepository) {

    @AssistedFactory
    interface Factory{
        fun create(view : CharacterInterface, chinId : Int) : CharacterPresenter
    }

    private companion object{
        val isExamplesEnabled = true
        val maxExampsLength = 20
    }

    private lateinit var showDictionaryItem: DictionaryItem

    private lateinit var redirectedDictionaryItem: DictionaryItem

    private lateinit var makeSoundClicked : PublishSubject<String>
    private var pinPlayer : PinPlayer? = null

    fun onCreate(){

        showDictionaryItem = getShowedChar()?: DictionaryItem(Int.MAX_VALUE, "-","-", emptyArray(),"-", emptyArray())

        makeSoundClicked = PublishSubject.create()

        initViewHeader()
        initTranslations()
        initExamples()
        initEntryChins()

    }

    fun addCardClicked(){
        view.startAddNewStudy(showDictionaryItem)
    }

    fun makeSoundClicked(){
        makeSoundClicked.onNext(showDictionaryItem.pinyin)
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

    private fun getShowedChar(): DictionaryItem?{
        var provided = dictionaryItemRepository.findById(chinId)

        val translations = provided!!.translation

        val linked = translations.find { it.contains("\$link") }

        if (linked != null){
            if (!translations.filterNot { it.contains("\$link") }.any {
                        it.contains(Regex("""[А-яа-я]""")) }
            ) {
                redirectedDictionaryItem = provided
                provided = dictionaryItemRepository.findByChinese(linked.substring(linked.indexOf("{") + 1, linked.indexOf("}"))).firstOrNull()
            }
        }
        return  provided
    }

    private fun initViewHeader(){
        val links = findLinked()

        if (showDictionaryItem.chineseOld != null){
            view.setLinked(listOf(showDictionaryItem.chineseOld!!))
        }
        else if (links.isNotEmpty()){
            view.setLinked(links.map { it.chinese })
        }

        val tag = showDictionaryItem.generalTag
        if (tag.isNotEmpty()){
            view.setTags(listOf(tag))
        }

        view.setChin( showDictionaryItem.chinese )
        view.setPinyin( showDictionaryItem.pinyin )
    }

    private fun initTranslations(){

        val result = mutableListOf<String>()

        val tags = showDictionaryItem.specialTag
        val translations = showDictionaryItem.translation

        for (i in translations.indices){

            if (translations[i].contains("\$link") /*|| !translations[i].contains(Regex("""[А-Яа-я]"""))*/)
                continue

            val line = (tags[i].toUpperCase())+" "+translations[i].capitalize()
            result.add(line)
        }

        view.setTranslations(result)
    }

    private fun initExamples(){
        Single.defer { Single.just {
            val matchExamples = exampleRepository.fundByChinCharId(chinId, maxExampsLength)
            return@just matchExamples.subList(
                0,
                if (matchExamples.size > maxExampsLength) maxExampsLength else matchExamples.lastIndex + 1
            )
        }.subscribeOn(Schedulers.io())
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({onSuccess ->
                if (onSuccess.invoke().isNotEmpty() && isExamplesEnabled){
                    view.setExamples(onSuccess.invoke())
                }
            },{ onError ->

            })
    }

    private fun initEntryChins(){
        if (showDictionaryItem.chinese.length > 1){
            val entries = mutableListOf<DictionaryItem>()

            for (char in showDictionaryItem.chinese.toList().map { it.toString() }){
                val chins = dictionaryItemRepository.findByChinese(char)

                for (chin in chins) {
                    if (chin.chinese.trim() == char
                            && showDictionaryItem.pinyin.contains(chin.pinyin)){
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

    private fun findLinked(): List<DictionaryItem>{
        val links = showDictionaryItem.translation.filterIndexed { _, s -> s.contains("\$link")  }

        val result = mutableMapOf<Int,DictionaryItem>()

        if (links.isNotEmpty()){
            for (link in links){
                val char = link.substring(link.indexOf("{")+1, link.indexOf("}"))

                val chins = dictionaryItemRepository.findByChinese(char)

                chins.forEach { result[it.id] = it }
            }
        }

        if (::redirectedDictionaryItem.isInitialized)
            result[redirectedDictionaryItem.id] = redirectedDictionaryItem

        return result.values.toList()
    }

}
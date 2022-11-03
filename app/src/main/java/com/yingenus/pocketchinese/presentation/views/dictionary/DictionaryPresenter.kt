package com.yingenus.pocketchinese.presentation.views.dictionary

import android.content.Context
import android.util.Log
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

import com.yingenus.pocketchinese.common.Result
import com.yingenus.pocketchinese.ISettings
import com.yingenus.pocketchinese.controller.logErrorMes
import com.yingenus.pocketchinese.domain.dto.DictionaryItem
import com.yingenus.pocketchinese.domain.repository.DictionaryItemRepository
import com.yingenus.pocketchinese.domain.entities.dictionarysearch.Language
import com.yingenus.pocketchinese.domain.entities.dictionarysearch.witchLanguage
import com.yingenus.pocketchinese.domain.usecase.WordsSearchUseCase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class DictionaryPresenter @AssistedInject constructor(
    @Assisted val  view: DictionaryInterface,
    private  val dictionaryItemRepository: DictionaryItemRepository,
    private val wordsSearchUseCase: WordsSearchUseCase,
    private val appSettings: ISettings) {

    @AssistedFactory
    interface Factory{
        fun create(view: DictionaryInterface) : DictionaryPresenter
    }

    private var searchType = WordsSearchUseCase.SearchParams.SearchType.FUZZY

    private var typeChangedPublisher : PublishSubject<WordsSearchUseCase.SearchParams.SearchType>
        = PublishSubject.create<WordsSearchUseCase.SearchParams.SearchType>()
    private lateinit var searchObservable: Observable<Pair<WordsSearchUseCase.SearchParams.SearchType,String>>

    fun onCreate(){
        registerSubscribers()
        updateHistory()
    }

    fun searchTypeChanged(newState: DictionaryInterface.States){
        if (newState == DictionaryInterface.States.SORT_FUZZY)
            searchType = WordsSearchUseCase.SearchParams.SearchType.FUZZY
        else if(newState == DictionaryInterface.States.SORT_MATCH)
            searchType = WordsSearchUseCase.SearchParams.SearchType.MATCH

        typeChangedPublisher.onNext(searchType)
    }

    private fun getSearchObserver(): Observable<Pair<WordsSearchUseCase.SearchParams.SearchType,String>>{
        if (::searchObservable.isLateinit){
            val typeChangedObs = typeChangedPublisher
                    .map { it to view.getSearchQuery() }.observeOn(Schedulers.io()).filter { it.second.isNotEmpty() }

            searchObservable = Observable.merge(typeChangedObs,view.getSearchObserver().map { searchType to it })
        }
        return searchObservable
    }

    private fun registerSubscribers(){

        val searchObserver = getSearchObserver()
        val filtered = searchObserver.filter {
            if(witchLanguage(it.second) == Language.RUSSIAN){
                it.second.length > 1
            }else{
                !it.second.isEmpty()
            }
        }
        filtered.subscribe { view.showSearchEmptyQuery(false) }
        val notPass = searchObserver.filter {
            if(witchLanguage(it.second) == Language.RUSSIAN){
                it.second.length <= 1
            }else{
                it.second.isEmpty()
            }
        }
        notPass.subscribe { view.showSearchEmptyQuery(true) }

        //.observeOn(Schedulers.io())
        //.observeOn(Schedulers.single())
        filtered
                .debounce(150, TimeUnit.MILLISECONDS)
                .switchMap { query ->
                    val results = wordsSearchUseCase
                        .search(query.second, WordsSearchUseCase.SearchParams(query.first))
                        .subscribeOn(Schedulers.single())
                        .publish()
                        .autoConnect(3)
                    val indexes = results.scan( 0){ acc , _ -> acc + 1 }
                    val count = results.count().filter { it!! == 0L }.map { Result.Empty<Pair<Int,DictionaryItem>>() }

                    indexes.zipWith(results, BiFunction<Int,DictionaryItem,Result<Pair<Int,DictionaryItem>>> { index, result-> Result.Success( index to result)})
                            .mergeWith(count).map { query.second to it}

                } 
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { msg ->
                    Log.i("Dictionary", "Cant compl Query = ${msg.logErrorMes()}")
                    view.showSearchError(msg.toString());
                }
                .doOnNext { next ->
                    val query = view.getSearchQuery()
                    if(query == next.first){
                        view.showSearchingAnimation(false)
                    }
                    if (next.second is Result.Empty){
                        view.clearSearchResult()
                        view.showSearchEmptyQuery(false)
                        view.showSearchNothing(true)
                    }else if(next.second is Result.Success) {
                        if ((next.second as Result.Success<Pair<Int, DictionaryItem>>).value.first == 0) {
                            view.clearSearchResult()
                        }
                        view.showSearchEmptyQuery(false)
                        view.showSearchNothing(false)
                        view.setSearchResult((next.second as Result.Success<Pair<Int, DictionaryItem>>).value)
                    }
                }
                .retry()
                .subscribe()

    }

    fun chinCharClicked(dictionaryItem: DictionaryItem){
        view.showChinChar(dictionaryItem)
        appSettings.addSearchItem(dictionaryItem.id)
        updateHistory()
    }

    fun onDestroy(){
        //searcher.close()
    }

    fun onResume(){
        updateHistory()
    }

    private fun updateHistory(){
        view.setHistory(getHistory())
    }


    fun getHistory(): List<DictionaryItem>{
        val history = mutableListOf<DictionaryItem>()


        val ids = appSettings.getSearchHistory()

        for (id in ids){
            val chinChar = dictionaryItemRepository.findById(id)
            if (chinChar != null )history.add(chinChar)
        }
        return history

    }

}
package com.yingenus.pocketchinese.presenters

import android.content.Context
import android.util.Log
import com.yingenus.pocketchinese.common.Result
import com.yingenus.pocketchinese.controller.Settings
import com.yingenus.pocketchinese.presentation.views.dictionary.DictionaryInterface
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

class DictionaryPresenter(
        val  view: DictionaryInterface,
        private  val dictionaryItemRepository: DictionaryItemRepository,
        private val wordsSearchUseCase: WordsSearchUseCase) {

    //private val searcher = DictionarySearch()

    private var searchType = WordsSearchUseCase.SearchParams.SearchType.FUZZY

    private lateinit var typeChangedPublisher : PublishSubject<WordsSearchUseCase.SearchParams.SearchType>
    private lateinit var searchObservable: Observable<Pair<WordsSearchUseCase.SearchParams.SearchType,String>>

    fun onCreate( context: Context){

        //searcher.initDictionary(dictionaryItemRepository, context)

        typeChangedPublisher = PublishSubject.create<WordsSearchUseCase.SearchParams.SearchType>()

        registerSubscribers()
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
        val filtered = searchObserver.filter { it.second.isEmpty()||(!(witchLanguage(it.second) == Language.RUSSIAN && it.second.length == 1)) }
        filtered.subscribe { view.showSearchEmptyQuery(true) }
        val notPass = searchObserver.filter { !(it.second.isEmpty()||(!(witchLanguage(it.second) == Language.RUSSIAN && it.second.length == 1))) }
        notPass.subscribe { view.showSearchEmptyQuery(true) }

        //.observeOn(Schedulers.io())
        //.observeOn(Schedulers.single())
        filtered
                .debounce(150, TimeUnit.MILLISECONDS)
                .switchMap { query ->
                    val results = wordsSearchUseCase.search(query.second, WordsSearchUseCase.SearchParams(query.first))
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
    }

    fun onDestroy(){
        //searcher.close()
    }


    fun getHistory(context: Context): List<DictionaryItem>{
        val history = mutableListOf<DictionaryItem>()

        if (context != null){
            val ids = Settings.getSearchHistory(context!!)

            for (id in ids){
                val chinChar = dictionaryItemRepository.findById(id)
                if (chinChar != null )history.add(chinChar)
            }
            return history
        }

        return emptyList()
    }

}
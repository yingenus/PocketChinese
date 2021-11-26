package com.yingenus.pocketchinese.presenters

import android.content.Context
import android.util.Log
import com.yingenus.pocketchinese.controller.Settings
import com.yingenus.pocketchinese.presentation.views.dictionary.DictionaryInterface
import com.yingenus.pocketchinese.controller.logErrorMes
import com.yingenus.pocketchinese.domain.dto.ChinChar
import com.yingenus.pocketchinese.domain.repository.ChinCharRepository
import com.yingenus.pocketchinese.model.dictionary.DictionarySearch
import com.yingenus.pocketchinese.model.dictionary.Language
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class DictionaryPresenter(val  view: DictionaryInterface,private  val chinCharRepository: ChinCharRepository) {

    private val searcher = DictionarySearch()


    private lateinit var typeChangedPublisher : PublishSubject<DictionaryInterface.States>
    private lateinit var searchObservable: Observable<String>

    fun onCreate( context: Context){

        searcher.initDictionary(chinCharRepository, context)

        typeChangedPublisher = PublishSubject.create<DictionaryInterface.States>()

        registerSubscribers()
    }

    fun searchTypeChanged(newState: DictionaryInterface.States){
        if (newState == DictionaryInterface.States.SORT_FUZZY)
            searcher.searchType = DictionarySearch.SearchFormat.FUZZY
        else if(newState == DictionaryInterface.States.SORT_MATCH)
            searcher.searchType = DictionarySearch.SearchFormat.MATCH

        typeChangedPublisher.onNext(newState)
    }

    private fun getSearchObserver(): Observable<String>{
        if (::searchObservable.isLateinit){
            val typeChangedObs = typeChangedPublisher
                    .map { view.getSearchQuery() }.observeOn(Schedulers.io()).filter { it.isNotEmpty() }

            searchObservable = Observable.merge(typeChangedObs,view.getSearchObserver())
        }
        return searchObservable
    }

    private fun registerSubscribers(){
        getSearchObserver()
                .filter { it.isEmpty()||(!(searcher.whichLanguage(it) == Language.RUSSIAN && it.length == 1)) }
                .observeOn(Schedulers.io())
                .debounce(150, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.single())
                .map { query ->
                   if(query.isEmpty()){
                       DictionaryInterface.Results.NoQuery
                   }else{
                       val result = searcher.search(query)
                       if(result.isEmpty()){
                           DictionaryInterface.Results.NoMatches
                       }else{
                           DictionaryInterface.Results.Matches(result)
                       }
                   }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { msg ->
                    Log.i("Dictionary", "Cant compl Query = ${msg.logErrorMes()}")
                    view.setResults(DictionaryInterface.Results.NoQuery)
                }
                .retry()
                .subscribe {
                    view.setResults(it)
                }

    }

    fun chinCharClicked(chinChar: ChinChar){
        view.showChinChar(chinChar)
    }

    fun onDestroy(){
        searcher.close()

    }


    fun getHistory(context: Context): List<ChinChar>{
        val history = mutableListOf<ChinChar>()

        if (context != null){
            val ids = Settings.getSearchHistory(context!!)

            for (id in ids){
                val chinChar = chinCharRepository.findById(id)
                if (chinChar != null )history.add(chinChar)
            }
            return history
        }

        return emptyList()
    }

}
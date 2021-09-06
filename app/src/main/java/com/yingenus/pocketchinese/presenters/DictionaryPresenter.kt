package com.yingenus.pocketchinese.presenters

import android.content.Context
import android.util.Log
import com.yingenus.pocketchinese.controller.Settings
import com.yingenus.pocketchinese.controller.fragment.DictionaryInterface
import com.yingenus.pocketchinese.controller.logErrorMes
import com.yingenus.pocketchinese.model.database.DictionaryDBOpenManger
import com.yingenus.pocketchinese.model.database.dictionaryDB.ChinChar
import com.yingenus.pocketchinese.model.database.dictionaryDB.ChinCharDaoImpl
import com.yingenus.pocketchinese.model.database.dictionaryDB.DictionaryDBHelper
import com.yingenus.pocketchinese.model.dictionary.DictionarySearch
import com.yingenus.pocketchinese.model.dictionary.Language
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject

class DictionaryPresenter(val  view: DictionaryInterface ) {

    private val searcher = DictionarySearch()

    private lateinit var charDaoImpl: ChinCharDaoImpl

    private lateinit var typeChangedPublisher : PublishSubject<DictionaryInterface.States>
    private lateinit var searchObservable: Observable<String>

    fun onCreate( context: Context){
        //DictionaryDBOpenManger.setOpenHelperClass(DictionaryDBHelper::class.java)


        val helper = DictionaryDBOpenManger.getHelper(context,DictionaryDBHelper::class.java)

        charDaoImpl = ChinCharDaoImpl(helper.connectionSource)

        searcher.initDictionary(charDaoImpl, context)

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
                .filter { it.isEmpty() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({onNext -> view.showEmpty()},{onError -> })

        getSearchObserver()
                .filter { it.isNotEmpty() }
                .filter { !(searcher.whichLanguage(it) == Language.RUSSIAN && it.length == 1) }
                .observeOn(Schedulers.io())
                .map { query ->
                    searcher.search(query)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { msg ->
                    Log.i("Dictionary", "Cant compl Query = ${msg.logErrorMes()}")
                    view.showEmpty()
                }
                .retry()
                .subscribe {
                    view.showItems(it)
                }

    }

    fun chinCharClicked(chinChar: ChinChar){
        view.showChinChar(chinChar)
    }

    fun onDestroy(){
        searcher.close()
        DictionaryDBOpenManger.releaseHelper()
    }


    fun getHistory(context: Context): List<ChinChar>{
        val history = mutableListOf<ChinChar>()

        if (context != null && ::charDaoImpl.isInitialized){
            val ids = Settings.getSearchHistory(context!!)

            for (id in ids){
                history.add(charDaoImpl.queryForId(id.toString()))
            }
            return history
        }

        return emptyList()
    }

}
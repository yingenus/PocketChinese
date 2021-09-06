package com.yingenus.pocketchinese.controller.fragment

import com.yingenus.pocketchinese.model.database.dictionaryDB.ChinChar
import io.reactivex.rxjava3.core.Observable

interface DictionaryInterface {

    enum class States{
        SORT_FUZZY, SORT_MATCH
    }

    fun getSearchQuery():String
    fun setSearchStates(state: States)
    fun showEmpty()
    fun getSearchObserver() : Observable<String>
    fun setHistory(history : List<ChinChar>)
    fun showItems(results: List<ChinChar>)
    fun showChinChar(chinChar: ChinChar)
}
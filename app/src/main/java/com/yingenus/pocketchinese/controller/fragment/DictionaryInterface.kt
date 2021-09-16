package com.yingenus.pocketchinese.controller.fragment

import com.yingenus.pocketchinese.model.database.dictionaryDB.ChinChar
import io.reactivex.rxjava3.core.Observable

interface DictionaryInterface {

    enum class States{
        SORT_FUZZY, SORT_MATCH
    }

    sealed class Results{
        object NoQuery : Results()
        object NoMatches : Results()
        class Matches(val chars : List<ChinChar>) : Results()
    }

    fun getSearchQuery():String
    fun setSearchStates(state: States)
    fun getSearchObserver() : Observable<String>
    fun setHistory(history : List<ChinChar>)
    fun setResults(results: Results)
    fun showChinChar(chinChar: ChinChar)
}
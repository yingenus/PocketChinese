package com.yingenus.pocketchinese.presentation.views.dictionary

import io.reactivex.rxjava3.core.Observable

interface DictionaryInterface {

    enum class States{
        SORT_FUZZY, SORT_MATCH
    }

    sealed class Results{
        object NoQuery : Results()
        object NoMatches : Results()
        class Matches(val chars : List<com.yingenus.pocketchinese.domain.dto.ChinChar>) : Results()
    }

    fun getSearchQuery():String
    fun setSearchStates(state: States)
    fun getSearchObserver() : Observable<String>
    fun setHistory(history : List<com.yingenus.pocketchinese.domain.dto.ChinChar>)
    fun setResults(results: Results)
    fun showChinChar(chinChar: com.yingenus.pocketchinese.domain.dto.ChinChar)
}
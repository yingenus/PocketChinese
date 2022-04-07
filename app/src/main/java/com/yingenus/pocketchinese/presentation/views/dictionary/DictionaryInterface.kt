package com.yingenus.pocketchinese.presentation.views.dictionary

import com.yingenus.pocketchinese.common.Result
import com.yingenus.pocketchinese.domain.dto.DictionaryItem

import io.reactivex.rxjava3.core.Observable

interface DictionaryInterface {

    enum class States{
        SORT_FUZZY, SORT_MATCH
    }

    sealed class Results{
        object NoQuery : Results()
        object NoMatches : Results()
        class Matches(val dictionaryItems : List<DictionaryItem>) : Results()
    }

    fun getSearchQuery():String
    fun setSearchStates(state: States)
    fun getSearchObserver() : Observable<String>
    fun setHistory(history : List<DictionaryItem>)
    fun setSearchResult(item : Pair<Int,DictionaryItem>)
    fun clearSearchResult()
    fun showSearchError(msg : String)
    fun showSearchNothing(show: Boolean)
    fun showSearchEmptyQuery(show: Boolean)
    fun showSearchingAnimation(show : Boolean)
    fun showChinChar(dictionaryItem: DictionaryItem)
}
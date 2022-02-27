package com.yingenus.pocketchinese.domain.usecase

import com.yingenus.pocketchinese.domain.dto.DictionaryItem
import com.yingenus.pocketchinese.domain.entities.dictionarysearch.SearchEngine
import io.reactivex.rxjava3.core.Observable

class WordSearchUseCaseImpl(
        private val matchSearch : SearchEngine,
        private val fuzzySearch : SearchEngine
) : WordsSearchUseCase {
    override fun search(query: String, params: WordsSearchUseCase.SearchParams): Observable<DictionaryItem> {
        val _query = query.toLowerCase()
        return when(params.searchType){
            WordsSearchUseCase.SearchParams.SearchType.FUZZY -> fuzzySearch.find(_query)
            WordsSearchUseCase.SearchParams.SearchType.MATCH -> matchSearch.find(_query)
        }
    }
}
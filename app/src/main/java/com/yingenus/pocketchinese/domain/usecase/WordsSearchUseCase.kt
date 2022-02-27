package com.yingenus.pocketchinese.domain.usecase

import com.yingenus.pocketchinese.domain.dto.DictionaryItem
import io.reactivex.rxjava3.core.Observable

interface WordsSearchUseCase {

    open class SearchParams(val searchType: SearchType){
        enum class SearchType{
            FUZZY, MATCH
        }
    }

    fun search(query : String, params : SearchParams): Observable<DictionaryItem>
}
package com.yingenus.pocketchinese.domain.entities.dictionarysearch


import com.yingenus.pocketchinese.domain.dto.DictionaryItem
import io.reactivex.rxjava3.core.Observable

interface SearchEngine {
    fun find( query: String): Observable<DictionaryItem>
}
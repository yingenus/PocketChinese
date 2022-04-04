package com.yingenus.pocketchinese.domain.entitiys.dictionary.search

import com.yingenus.pocketchinese.domain.entitiys.dictionary.search.resours.Sample

fun Sample.searchResults(): List<ResSearch.SearchResult>{
    return this.ids.mapIndexed { index: Int, id: Int -> ResSearch.SearchResult(id, wights[index]) }
}
fun Sample.searchResultsUnique(): Set<ResSearch.SearchResult>{
    val result = mutableSetOf<ResSearch.SearchResult>()
    this.ids.forEachIndexed { index, id ->
        result.add(ResSearch.SearchResult(id, wights[index]))
    }
    return result
}
package com.yingenus.pocketchinese.domain.entitiys.dictionary.search.sort

import com.yingenus.pocketchinese.domain.entitiys.dictionary.search.N2GramResultDetailed
import com.yingenus.pocketchinese.domain.entitiys.dictionary.search.ResSearch
import java.io.InputStream

class N2GramSortedSearch(search: ResSearch): SearchDecorator(search){

    override fun searchSentence(sentence: String, vararg resources: InputStream): List<ResSearch.SearchResult> {
        val searchResults = super.searchSentence(sentence, *resources)

        return try {
            sort(searchResults as List<N2GramResultDetailed>)
        }catch (e : ClassCastException){
            searchResults.toList()
        }
    }

    private fun sort(items: List<N2GramResultDetailed>): List<N2GramResultDetailed> {
        return items.sortedWith(compareByDescending<N2GramResultDetailed>{ it.N2Entries}.thenByDescending{it.N1Entries}.thenBy{it.wight})
    }
}
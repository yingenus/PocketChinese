package com.yingenus.pocketchinese.model.dictionary.search.sort

import com.yingenus.pocketchinese.model.dictionary.search.LevenshteinResultDetailed
import com.yingenus.pocketchinese.model.dictionary.search.ResSearch
import java.io.InputStream

class LevenshteinSortedSearch(search: ResSearch): SearchDecorator(search){

    override fun searchSentence(sentence: String, vararg resources: InputStream): List<ResSearch.SearchResult> {
        val searchResults = super.searchSentence(sentence, *resources)

        return try {
            sort(searchResults as List<LevenshteinResultDetailed>)
        }catch (e : ClassCastException){
            searchResults.toList()
        }
    }

    private fun sort(items: List<LevenshteinResultDetailed>): List<LevenshteinResultDetailed> {
        return items.sortedWith(compareByDescending<LevenshteinResultDetailed>{ it.entry }.thenBy{it.distance}.thenBy {it.wight })
    }
}
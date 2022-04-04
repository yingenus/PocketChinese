package com.yingenus.pocketchinese.domain.entitiys.dictionary.search.sort

import com.yingenus.pocketchinese.domain.entitiys.dictionary.search.ResSearch
import java.io.InputStream

open class SearchDecorator(val search : ResSearch) : ResSearch {

    override fun searchSentence(sentence: String, vararg resources: InputStream): List<ResSearch.SearchResult> {
        return search.searchSentence(sentence, *resources)
    }

}
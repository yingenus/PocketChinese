package com.yingenus.pocketchinese.model.dictionary.search

open class LevenshteinResultDetailed( id : Int, wight : Int) : ResSearch.SearchResult( id , wight ) {
    open var distance = 0
    open var entry = 0
}
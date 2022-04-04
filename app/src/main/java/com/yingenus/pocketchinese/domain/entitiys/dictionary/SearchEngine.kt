package com.yingenus.pocketchinese.domain.entitiys.dictionary


import android.util.Log

class SearchEngine(val queryHandler: Searcher, val sentenceExtractor: Extractor ){

    private val maxLength = 100;

    fun startSearch(query: String): List<com.yingenus.pocketchinese.domain.dto.ChinChar>{
        Log.i("SearchEngine", "search query: $query")
        val searcherResult = queryHandler.search(query)
        Log.i("SearchEngine", "search finish")

        return extractChins(searcherResult)
    }

    private fun extractChins(reqList: List<String>): List<com.yingenus.pocketchinese.domain.dto.ChinChar>{
        Log.i("SearchEngine", "extract")
        val extracted = mutableMapOf<Int, com.yingenus.pocketchinese.domain.dto.ChinChar>()

        val cutedList = if (reqList.size > maxLength)
            reqList.subList(0, maxLength)
        else reqList

        for (req in cutedList){

            for (item in sentenceExtractor.extract(req)){

                if (!extracted.containsKey(item.id)){
                    extracted[item.id] = item
                }
            }
        }
        Log.i("SearchEngine", "extract finish, total: ${extracted.size}")
        return extracted.values.toList()
    }
}
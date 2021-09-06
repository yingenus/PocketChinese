package com.yingenus.pocketchinese.model.dictionary.search

import com.yingenus.pocketchinese.model.dictionary.search.resours.SamplesReader
import java.io.InputStream


class MatchSearch : ResSearch {

    override fun searchSentence(sentence: String, vararg resorces : InputStream): List<ResSearch.SearchResult> {
        val searchedSent = prepareSent(sentence)
        val samplesReader = SamplesReader(resorces[0].bufferedReader())

        val resultsMap = mutableMapOf<Int, ResSearch.SearchResult>()

        samplesReader.use { reader ->
            var sample = reader.readSample()

            while (sample != null) {

                if (sample.sentence.contains(searchedSent)) {
                    for (searchResult in
                    sample.ids.mapIndexed { index: Int, id: Int -> ResSearch.SearchResult(id, sample!!.wights[index]) }) {

                        resultsMap[searchResult.hashCode()] = searchResult
                    }
                }
                sample = reader.readSample()
            }

        }

        return resultsMap.values.toList()
    }

    private fun prepareSent(sentence: String) = sentence.toLowerCase().trim()

}
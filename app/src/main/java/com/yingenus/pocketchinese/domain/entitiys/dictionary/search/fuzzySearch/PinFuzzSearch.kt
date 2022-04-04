package com.yingenus.pocketchinese.domain.entitiys.dictionary.search.fuzzySearch

import com.yingenus.pocketchinese.domain.entitiys.dictionary.search.LevenshteinResultDetailed
import com.yingenus.pocketchinese.domain.entitiys.dictionary.search.ResSearch
import com.yingenus.pocketchinese.domain.entitiys.dictionary.search.WordsIndexer
import com.yingenus.pocketchinese.domain.entitiys.dictionary.search.pin.PinIndexer
import com.yingenus.pocketchinese.domain.entitiys.dictionary.search.resours.SamplesReader
import com.yingenus.pocketchinese.domain.entitiys.dictionary.search.searchResults

import org.apache.commons.text.similarity.LevenshteinDetailedDistance
import java.io.InputStream


class PinFuzzSearch : ResSearch {
    private companion object {
        val lengthThreshold = mapOf( 0 to 0 , 3 to 1, 4 to 2, 6 to 3 , 8 to 4 )
    }

    private val indexer = PinIndexer()

    private lateinit var sampleWords : Array<WordsIndexer.Word>

    override fun searchSentence(sentence: String, vararg resources : InputStream): List<LevenshteinResultDetailed> {

        val levRes = SamplesReader(resources[0].bufferedReader())

        val results = LevenshteinSearcher(levRes)
                .searchWord(prepare(sentence))

        return results.flatMap { entry -> entry.value.map { item -> LevenshteinResultDetailed(item.idPlus, item.wight).apply { distance = entry.key } } }
    }

    private fun prepare(value : String): String = value.toLowerCase().trim()

    private class LevenshteinSearcher( val levReader : SamplesReader){

        fun getThreshold(length : Int): Int{

            val keys = lengthThreshold.keys.toIntArray().sorted()

            for ( i in 0.until(keys.size)){
                if (length < keys[i]){
                    return lengthThreshold[keys[i-1]] ?: error("")
                }
            }
            return lengthThreshold[keys.last()] ?: error("")
        }


        fun searchWord( word : String ): Map<Int, List<ResSearch.SearchResult>> {

            val levenshtein = LevenshteinDetailedDistance(getThreshold(word.length))

            val outMap = mutableMapOf<Int, MutableList<ResSearch.SearchResult>>()

            levReader.use { reader: SamplesReader ->
                var sample = reader.readSample()

                while (sample != null) {

                    val samp = if (word.length > sample.sentence.length)
                        sample.sentence
                    else
                        sample.sentence.substring(0, word.length)

                    val result = levenshtein.apply(word, samp)

                    if (result.distance != -1) {

                        val searchResults = sample.searchResults()

                        if (outMap.containsKey(result.distance)) {
                            outMap[result.distance]!!.addAll(searchResults)
                        } else {
                            outMap[result.distance] = searchResults.toMutableList()
                        }
                    }

                    sample = reader.readSample()
                }

            }
            return outMap
       }

    }
}
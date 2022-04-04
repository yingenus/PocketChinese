package com.yingenus.pocketchinese.domain.entitiys.dictionary.search.fuzzySearch

import com.yingenus.pocketchinese.domain.entitiys.dictionary.search.LevenshteinResultDetailed
import com.yingenus.pocketchinese.domain.entitiys.dictionary.search.ResSearch
import com.yingenus.pocketchinese.domain.entitiys.dictionary.search.resours.SamplesReader
import com.yingenus.pocketchinese.domain.entitiys.dictionary.search.searchResults
import com.yingenus.pocketchinese.domain.entitiys.dictionary.search.searchResultsUnique
import org.apache.commons.text.similarity.LevenshteinDetailedDistance
import org.apache.commons.text.similarity.LevenshteinResults
import java.io.InputStream

class RusFuzzSearch : ResSearch {

    private companion object {
        val lengthThreshold = mapOf( 0 to 0 , 4 to 1, 6 to 2 , 10 to 3 )

        fun ResSearch.SearchResult.toLevResultDetailed(): LevenshteinResultReff = LevenshteinResultReff (this.idPlus, this.wight)

    }

    //private val indexer: RusIndexer = RusIndexer()

    //private lateinit var sampleWords : Array<Word>

    override fun searchSentence(sentence: String, vararg resources : InputStream): List<LevenshteinResultDetailed> {

        val reader = SamplesReader(resources[0].bufferedReader())


        return Engine(reader).search(prepareSent(sentence))
    }

    private fun prepareSent(sentence: String) = sentence.toLowerCase().trim()

    private class Engine(val levReader: SamplesReader){

        val levenshteinSearcher = LevenshteinSearcher()

        fun search(sentence: String): List<LevenshteinResultDetailed>{

            val words = sentence.split(" ").map { WordContainer(it.trim()) }
            val matchesMap = mutableMapOf<Int, LevenshteinResultReff>()

            levReader.use {reader : SamplesReader ->
                var sample = reader.readSample()

                while (sample != null) {

                    //val addedResult = mutableListOf<Int>()
                    //val bestDist = -1

                    val results =
                            words.map { Pair(it,levenshteinSearcher.searchWord(it.word,sample!!.sentence)) }
                                    .filter { it.second.distance != -1 }
                                    .sortedByDescending { it.second.distance }

                    val searchResults = sample.searchResults().toMutableList()

                    results.forEachIndexed { index: Int , result: Pair<WordContainer,LevenshteinResults> ->
                        val list = if( index == 0) sample!!.searchResultsUnique() else searchResults.toList()
                        list.forEach{searchResult ->
                            if (!matchesMap.containsKey(searchResult.hashCode())){
                                matchesMap[searchResult.hashCode()] = searchResult.toLevResultDetailed()
                            }
                            matchesMap[searchResult.hashCode()].let {
                                if ( (!it!!.wordsMap.containsKey(result.first.hashCode())) || it.wordsMap[result.first.hashCode()]!! < result.second.distance){
                                    it.wordsMap[result.first.hashCode()] = result.second.distance
                                    searchResults.remove(searchResult)
                                }
                            }

                        }
                    }

                    /*
                    for (word in words) {
                        val result = levenshteinSearcher.searchWord(word, sample.sentence)

                        if (result.distance != -1) {
                            if (addedResult.isEmpty()) {
                                for (searchResult in
                                sample.searchResultsUnique()) {
                                    if(!matchesMap.containsKey(searchResult.hashCode())){
                                        matchesMap[searchResult.hashCode()] = searchResult.toLevResultDetailed()
                                    }
                                    matchesMap[searchResult.hashCode()].let {
                                        it!!.distance += result.distance
                                        it!!.entry += 1
                                    }
                                    addedResult.add(searchResult.hashCode())
                                }
                            } else {
                                val sameResult =
                                        sample.searchResults().toMutableList()
                                addedResult.forEach { entry ->
                                    sameResult.removeAll { it.idPlus == entry }
                                }
                                for (same in sameResult.distinct()) {
                                    addedResult.add(same.hashCode())
                                    matchesMap[same.hashCode()].let {
                                        it!!.distance += result.distance
                                        it!!.entry += 1
                                    }
                                }

                            }
                        }
                    }

                     */
                    sample = reader.readSample()
                }
            }
            return matchesMap.values.toList()
        }
    }

    private class LevenshteinResultReff(id : Int, wight : Int) : LevenshteinResultDetailed(id,wight){
        override var distance: Int
            get() = wordsMap.values.reduce { acc, i -> acc+i }
            set(value) {}
        override var entry: Int
            get() = wordsMap.size
            set(value) {}

        val wordsMap = mutableMapOf<Int,Int>()

    }
    private class WordContainer(val word : String);

    private class LevenshteinSearcher{

        fun getThreshold(length : Int): Int{

            val keys = lengthThreshold.keys.toIntArray().sorted()

            for ( i in 0.until(keys.size)){
                if (length < keys[i]){
                    return lengthThreshold[keys[i-1]] ?: error("")
                }
            }
            return lengthThreshold[keys.last()] ?: error("")
        }


        fun searchWord(word : String , sent: String): LevenshteinResults {

            val levenshtein = LevenshteinDetailedDistance(getThreshold(word.length))

            val compered  = if (word.length < sent.length) sent.substring(0, word.length) else sent

            val result =  levenshtein.apply(word, compered)

            return result
        }

    }
}
package com.yingenus.pocketchinese.domain.entitiys.dictionary.search.fuzzySearch

import com.yingenus.pocketchinese.domain.entitiys.dictionary.search.N2GramResultDetailed
import com.yingenus.pocketchinese.domain.entitiys.dictionary.search.ResSearch
import com.yingenus.pocketchinese.domain.entitiys.dictionary.search.resours.SamplesReader
import com.yingenus.pocketchinese.domain.entitiys.dictionary.search.searchResults
import com.yingenus.pocketchinese.domain.entitiys.dictionary.search.searchResultsUnique
import java.io.InputStream
import java.lang.IllegalArgumentException

class ChnFuzzSearch : ResSearch {

    private companion object{
        fun ResSearch.SearchResult.toNGramResult(): N2GramResultDetailed {
            return N2GramResultDetailed(this.idPlus, this.wight)
        }
    }

    override fun searchSentence(sentence: String, vararg resources : InputStream): List<N2GramResultDetailed> {

        if (resources.size < 2){
            throw IllegalArgumentException("requires two streams for N1 and N2")
        }

        val n1reader = SamplesReader(resources[0].bufferedReader())
        val n2reader = SamplesReader(resources[1].bufferedReader())

        return Engine(n1reader,n2reader).search(sentence.prepareSentence())
    }

    private fun String.prepareSentence(): String = replace("""\s+""","").trim()


    private class Engine(val n1reader : SamplesReader, val n2reader : SamplesReader){


        fun get2NGrams(sentence: String): List<String>{
            val results = mutableListOf<String>()

            if (sentence.length>1){
                for (i in 2.rangeTo(sentence.length)){
                    val subSent = sentence.substring(i-2,i)
                    results.add(subSent)
                }
            }
            return results

        }

        fun get1NGrams(sentence: String) =
                sentence.map { it.toString() }


        fun search(sentence: String): List<N2GramResultDetailed>{
            val gramResults = mutableMapOf<Int, N2GramResultDetailed>()


            val n1grams = get1NGrams(sentence)

            n1reader.use { reader : SamplesReader ->
                var sample  = reader.readSample()

                while (sample != null) {

                    //val addedResult = mutableListOf<Int>()

                    val matchedWords = n1grams.filter { it == sample!!.sentence }

                    val searchResults = sample.searchResults().toMutableList()

                    matchedWords.forEachIndexed { index, _ ->

                        val list = if (index == 0) sample!!.searchResultsUnique() else searchResults.toList()

                        list.forEach { searchResult ->
                            if (!gramResults.containsKey(searchResult.hashCode())){
                                gramResults[searchResult.hashCode()] = searchResult.toNGramResult()
                            }
                            gramResults[searchResult.hashCode()]!!.N1Entries +=1
                            searchResults.remove(searchResult)
                        }
                    }

                    /*
                    for (n1gram in n1grams) {
                        if (n1gram == sample.sentence) {
                            if (addedResult.isEmpty()) {
                                for (searchResult in sample.searchResultsUnique()) {
                                    if (!gramResults.containsKey(searchResult.hashCode())){
                                        gramResults[searchResult.hashCode()] = searchResult.toNGramResult()
                                    }
                                    gramResults[searchResult.hashCode()].let { it!!.N1Entries += 1 }
                                    addedResult.add(searchResult.hashCode())
                                }
                            } else {
                                val sameResult = sample.searchResults().toMutableList()
                                addedResult.forEach { id : Int -> sameResult.removeAll { it.idPlus == id } }
                                for (same in sameResult.distinct()) {
                                    addedResult.add(same.hashCode())
                                    gramResults[same.hashCode()].let { it!!.N1Entries += 1 }
                                }
                            }


                        }
                    }

                     */
                    sample = reader.readSample()
                }
            }

            val n2grams = get2NGrams(sentence)

            n2reader.use { reader : SamplesReader ->
                var sample = reader.readSample()

                while (sample != null) {

                    val matchedWords = n2grams.filter { it == sample!!.sentence }

                    val searchResults = sample!!.searchResults().toMutableList()

                    matchedWords.forEachIndexed { index, _ ->

                        val list = if (index == 0) sample!!.searchResultsUnique() else searchResults.toList()

                        list.forEach { searchResult ->
                            gramResults[searchResult.hashCode()]!!.N2Entries +=1
                            searchResults.remove(searchResult)
                        }
                    }

                    /*
                    val addedResult = mutableListOf<Int>()

                    for (n2gram in n2grams) {
                        if (n2gram == sample!!.sentence) {
                            if (addedResult.isEmpty()) {
                                for (searchResult in sample!!.searchResultsUnique()) {
                                    if (gramResults.containsKey(searchResult.hashCode())) {
                                        gramResults[searchResult.hashCode()]!!.N2Entries += 1
                                        addedResult.add(searchResult.hashCode())
                                    }
                                }
                            } else {
                                val sameResult = sample!!.searchResults().toMutableList()
                                addedResult.forEach { id : Int -> sameResult.removeAll{it.idPlus == id} }
                                for (same in sameResult.distinct()) {
                                    addedResult.add(same.hashCode())
                                    gramResults[same.hashCode()]!!.N2Entries += 1
                                }
                            }
                        }
                    }

                     */
                    sample = reader.readSample()
                }
            }

            return gramResults.values.toList()
        }


    }
}
package com.yingenus.pocketchinese.domain.entities.dictionarysearch

import org.apache.commons.text.similarity.LevenshteinDetailedDistance
import org.apache.commons.text.similarity.LevenshteinResults

/*
    Сравнение слов с помощю растояния левенштейна
     @param {threshold} - колличесто ошибок при длене слова такой как указано и больше Pair<length,threshold>
*/
class LevenshteinComparator( private val thresholds : Set<Pair<Int,Int>>) : Comparator {

    // Определение возможного колличества ошибок взависемости от длинны слова
    fun getThreshold(length : Int): Int{

        var threshold : Int = 0
        for (i in thresholds){
            if ( length >= i.first){
                threshold = i.second
            }
        }

        return threshold
    }


    override fun compere(word: String, comparable: String): Int {
        val levenshtein = LevenshteinDetailedDistance(getThreshold(word.length))

        val comper =
                if (word.length < comparable.length)
                    comparable.substring( 0, word.length)
                else
                    comparable

        val result =  levenshtein.apply(word, comper)

        return result.distance
    }
}
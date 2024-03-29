package com.yingenus.pocketchinese.domain.entities.dictionarysearch

import com.yingenus.pocketchinese.common.Result
import com.yingenus.pocketchinese.domain.entities.dictionarysearch.ngram.NgramSearch
import com.yingenus.pocketchinese.domain.dto.VariantWord
import javax.inject.Inject
import javax.inject.Named


class TwoNgramsSearch @Inject constructor(
    @Named("chinese_1_gram") val n1gramSearch : NgramSearch<VariantWord>,
    @Named("chinese_2_gram") val n2gramSearch : NgramSearch<VariantWord>): Searcher {


    override fun find(query: String): Result<List<Int>>{
        val gramResults = mutableMapOf<VariantWord, Pair<Int,Int>>()

        val n1grams = n1gramSearch.find(query)

        if (n1grams is Result.Failure) return Result.Failure(n1grams.msg)

        if (n1grams is Result.Success) {

            n1grams.value.forEach {
                gramResults[it.candidate] = it.ngramEntry to 0
            }

        }

        val n2grams = n2gramSearch.find(query)

        if (n2grams is Result.Failure) return Result.Failure(n2grams.msg)

        if (n2grams is Result.Success) {

            n2grams.value.forEach {
                gramResults[it.candidate] = (gramResults[it.candidate]?.first?:0 ) to it.ngramEntry
            }

        }

        val sorted = gramResults.map { Triple(it.value.second, it.value.first, it.key) }
                .sortedWith(
                        compareByDescending<Triple<Int,Int, VariantWord>>{ it.second}
                                .thenByDescending { it.first }.thenBy { it.third.weight }).map { it.third.id }.distinct()


        return if (sorted.isEmpty()) Result.Empty() else Result.Success(sorted)
    }

    override fun release() {
        // to do nothing
    }
}
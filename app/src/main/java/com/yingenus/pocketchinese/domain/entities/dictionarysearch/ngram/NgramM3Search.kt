package com.yingenus.pocketchinese.domain.entities.dictionarysearch.ngram

import com.yingenus.pocketchinese.common.Result
import com.yingenus.pocketchinese.domain.repository.search.NgramM3Repository
import java.lang.IllegalArgumentException
import kotlin.math.max
import kotlin.math.min

class NgramM3Search<T>(
    val ngramRepository: NgramM3Repository<T>,
    val ngramLength : Int,
    val searchParams: Params = DefaultParams()
): NgramSearch<T> {

    interface Params{
        fun getThreshold( query : String): Int
    }

    class DefaultParams() : Params {
        override fun getThreshold(query: String): Int {
            return 2
        }
    }

    init {
        if(ngramLength < 2) throw IllegalArgumentException(" minimum length of ngram is 2 ")
    }

    override fun find(query: String): Result<List<NgramSearch.NgramResult<T>>> {

        val maxMistake = searchParams.getThreshold(query)

        val queryNgrams = split("_${query}_" , ngramLength)

        val candidates = mutableMapOf<Int, NgramSearch.NgramResult<T>>()

        queryNgrams.forEachIndexed{ index : Int, ngram : String ->
            if (index == 0){
                val finded = ngramRepository.getNgram(ngram,0)

                if (finded is Result.Failure) {
                    return Result.Failure(finded.msg)
                } else if (finded is Result.Success){
                    finded.value.forEach {
                        if (candidates.containsKey(it.hashCode())) {
                            candidates[it.hashCode()]!!.ngramEntry += 1
                        } else {
                            candidates[it.hashCode()] = NgramSearch.NgramResult<T>(it, 1)
                        }
                    }

                }
            }else {

                val starIndex = index - maxMistake
                val endIndex = index + maxMistake

                for (_index in max(1, starIndex)..min(endIndex, queryNgrams.lastIndex)) {

                    val finded = ngramRepository.getNgram(ngram, _index)

                    if (finded is Result.Failure) {
                        return Result.Failure(
                            finded.msg
                        )
                    } else if(finded is Result.Success){
                        finded.value.forEach {
                            if (candidates.containsKey(it.hashCode())) {
                                candidates[it.hashCode()]!!.ngramEntry += 1
                            } else {
                                candidates[it.hashCode()] = NgramSearch.NgramResult<T>(it, 1)
                            }
                        }
                    }
                }
            }
        }

        return if (candidates.isEmpty()) Result.Empty()
        else Result.Success(candidates.values.toList())
    }
}
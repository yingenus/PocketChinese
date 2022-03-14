package com.yingenus.pocketchinese.domain.entities.dictionarysearch

import com.yingenus.pocketchinese.common.Result
import com.yingenus.pocketchinese.domain.entities.dictionarysearch.ngram.NgramSearch
import com.yingenus.pocketchinese.domain.repository.search.UnitWordRepository
import com.yingenus.pocketchinese.domain.dto.UnitWord
import com.yingenus.pocketchinese.domain.dto.VariantWord
import kotlin.system.measureTimeMillis

class NComSearch(
        val unitWordRepository: UnitWordRepository,
        val ngramSearcher : NgramSearch<Int>,
        val comparator : Comparator
        ) : Searcher {


    override fun find(query: String): Result<List<Int>> {

        val queryWords = query.split(",")

        var ngrams = mutableMapOf<String, List<NgramSearch.NgramResult<Int>>>()
        val timeInMillis1 = measureTimeMillis {

            for (word in queryWords){
                val ngramResult = ngramSearcher.find(word)
                if (ngramResult is Result.Failure ) return Result.Failure( ngramResult.msg )
                else if ( ngramResult is Result.Empty) return Result.Empty()

                ngrams[word] = (ngramResult as Result.Success).value
            }


        }
        //println("(The operation search ngrams took $timeInMillis1 ms)")

        // Triple< Колличество попавших слов, Сумма растояний левенштейна всех слов ,VariantWord>
        val result = mutableMapOf<VariantWord,Triple<Int,Int, VariantWord>>()

        val minN = if (query.length == 1) 1 else 1

        val requestIds = ngrams.values.flatten().map { it.candidate }.distinct()
        val words : Result<List<UnitWord>>
        val timeInMillis2 = measureTimeMillis {
            words = unitWordRepository.getUnitWords(requestIds.toIntArray())
        }
        println("(The operation search words took $timeInMillis2 ms)")
        if (words is Result.Success){
            words.value
                .forEach { unitWord ->

                    for (word in queryWords){

                        val distance = comparator.compere(word,unitWord.word)
                        if (distance != -1){
                            unitWord.mentions.forEach { variant ->

                                if (result.contains(variant)){
                                    val old = result[variant]!!
                                    result[variant] = Triple(old.first +1, old.second + distance, variant)
                                }
                                else{
                                    result[variant] = Triple(1, distance, variant)
                                }
                            }
                        }
                    }
                }

            val sorted = result.values.sortedWith( compareByDescending<Triple<Int,Int, VariantWord>>{ it.first }.thenBy{it.second}.thenBy{it.third.weight})
            //val first100 = sorted.subList(0, min(100,sorted.size))

            return Result.Success(sorted.map { it.third.id })

        }else if (words is Result.Failure){
            return Result.Failure(words.msg)
        }else{
            return Result.Empty()
        }


    }
}
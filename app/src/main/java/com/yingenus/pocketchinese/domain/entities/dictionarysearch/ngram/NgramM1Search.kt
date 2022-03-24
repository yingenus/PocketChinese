package main.newsearch.NGramSearch

import com.yingenus.pocketchinese.common.Result
import com.yingenus.pocketchinese.domain.entities.dictionarysearch.ngram.NgramSearch
import com.yingenus.pocketchinese.domain.entities.dictionarysearch.ngram.split
import com.yingenus.pocketchinese.domain.repository.search.NgramRepository


class NgramM1Search<T>(val ngramRepository: NgramRepository<T>, val ngramLength : Int):
    NgramSearch<T> {

    override fun find(query: String): Result<List<NgramSearch.NgramResult<T>>> {
        val ngrams = split(query, ngramLength)

        val candidates = hashMapOf<Int, NgramSearch.NgramResult<T>>()

        ngrams.forEach { ngram ->

            val results = ngramRepository.getNgrams(ngram)

            if (results is Result.Failure)
                return Result.Failure(results.msg)
            else if (results is Result.Success){

                results.value.distinctBy { it.hashCode() }.forEach { result ->

                    if (candidates.containsKey(result.hashCode())){
                        candidates[result.hashCode()]!!.ngramEntry += 1
                    }else{
                        val ngramResult = NgramSearch.NgramResult<T>(result, 1)
                        candidates[result.hashCode()] = ngramResult
                    }
                }
            }
        }

        return if (candidates.isEmpty()) Result.Empty()
        else Result.Success(candidates.values.toList())
    }
}
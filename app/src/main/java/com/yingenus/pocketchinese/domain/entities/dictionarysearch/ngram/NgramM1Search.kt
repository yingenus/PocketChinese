package main.newsearch.NGramSearch

import com.yingenus.pocketchinese.common.Result
import com.yingenus.pocketchinese.domain.entities.dictionarysearch.ngram.NgramSearch
import com.yingenus.pocketchinese.domain.repository.search.NgramRepository


class NgramM1Search<T>(val ngramRepository: NgramRepository<T>, val ngramLength : Int):
    NgramSearch<T> {

    override fun find(query: String): Result<List<NgramSearch.NgramResult<T>>> {
        TODO("Not yet implemented")
    }
}
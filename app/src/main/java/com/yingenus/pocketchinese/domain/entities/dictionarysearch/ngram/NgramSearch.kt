package com.yingenus.pocketchinese.domain.entities.dictionarysearch.ngram

import com.yingenus.pocketchinese.common.Result

interface NgramSearch<T> {
    fun find(query : String) : Result<List<NgramResult<T>>>

    open class NgramResult<T>(val candidate: T, var ngramEntry : Int) {
    }
}
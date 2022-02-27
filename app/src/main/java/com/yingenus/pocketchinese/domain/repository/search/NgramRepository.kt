package com.yingenus.pocketchinese.domain.repository.search

import com.yingenus.pocketchinese.common.Result

interface NgramRepository<T> {
    fun getNgrams( ngram : String) : Result<List<T>>
}
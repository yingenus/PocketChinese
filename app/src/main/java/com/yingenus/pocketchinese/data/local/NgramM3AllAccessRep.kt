package com.yingenus.pocketchinese.data.local

import com.yingenus.pocketchinese.common.Result
import com.yingenus.pocketchinese.domain.repository.search.NgramM3Repository

interface NgramM3AllAccessRep<T> : NgramM3Repository<T> {
    fun getAllNgrams() : Result<List<Triple<String,Int, List<Int>>>>
}
package com.yingenus.pocketchinese.data.proxy

import com.yingenus.pocketchinese.common.Result


interface NgramM3Repository<T> {
    fun getNgrams( ngram : String, positions : IntArray) : Result<List<Pair<Int,List<Int>>>>
    fun getNgram( ngram : String, position : Int) : Result<List<T>>
}
package com.yingenus.pocketchinese.functions.search

import com.yingenus.pocketchinese.common.Result
import com.yingenus.pocketchinese.domain.entities.dictionarysearch.Searcher
import java.nio.charset.Charset

class NativeSearcher(val prefixSearcher: PrefixSearcher, val charset : Charset) : Searcher {
    override fun find(query: String): Result<List<Int>> {
        return try {
            val ids = prefixSearcher.find(query.toByteArray(charset))
            if (ids.isEmpty())
                Result.Empty()
            else
                Result.Success(ids.toList())
        }catch (e : Exception){
            Result.Failure(e.message?:"no message")
        }
    }

    override fun release() {
        prefixSearcher.dispose()
    }
}
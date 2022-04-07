package com.yingenus.pocketchinese.functions.search

import android.util.Log
import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.common.Result
import com.yingenus.pocketchinese.domain.entities.dictionarysearch.Searcher
import java.nio.charset.Charset
import kotlin.system.measureTimeMillis

class NativeSearcher(val prefixSearcher: PrefixSearcher, val charset : Charset) : Searcher {

    companion object{
        fun build(prefixSearcher: PrefixSearcher, language: Language): NativeSearcher{
            return NativeSearcher(prefixSearcher, getCharset(language))
        }
    }


    override fun find(query: String): Result<List<Int>> {
        return try {
            val ids : IntArray
            val time = measureTimeMillis {
                ids = prefixSearcher.find(query.toByteArray(charset))
            }
            Log.d("NativeSearcher"," The operation search $query took: $time ms")

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
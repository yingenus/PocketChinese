package com.yingenus.pocketchinese.model.dictionary

import android.content.res.AssetManager
import com.yingenus.pocketchinese.model.dictionary.search.ResSearch
import java.io.InputStream

interface Searcher {
    fun search(query : String): List<String>
}

class ResSearcher(val search : ResSearch,val assetsManager : AssetManager, val resFiles : List<String>): Searcher{
    override fun search(query: String): List<String> {
        val streams = getStreams()

        val results = search.searchSentence(query,*streams.toTypedArray())

        streams.forEach{ it.close()}

        return  results.distinctBy { it.id }.map { it.id.toString() }
    }

    private fun getStreams(): List<InputStream>{
        return resFiles.map { file -> assetsManager.open(file) }
    }

}
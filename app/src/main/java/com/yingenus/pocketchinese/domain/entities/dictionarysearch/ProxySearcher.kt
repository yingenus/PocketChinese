package com.yingenus.pocketchinese.domain.entities.dictionarysearch

import com.yingenus.pocketchinese.common.Result
import java.lang.IllegalArgumentException

class ProxySearcher: Searcher {

    private var searcher : Searcher? = null

    fun initSearcher( searcher: Searcher){
        if (this.searcher != null) throw IllegalStateException("ProxySearcher cant be initialaze twise")
        this.searcher = searcher
    }

    override fun find(query: String): Result<List<Int>> {
        if (searcher == null) throw IllegalArgumentException("proxy class not added")
        return searcher!!.find(query)
    }

    override fun release() {
        if (searcher == null) throw IllegalArgumentException("proxy class not added")
        return searcher!!.release()
    }
}
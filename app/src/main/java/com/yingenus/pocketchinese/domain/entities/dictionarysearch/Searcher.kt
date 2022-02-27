package com.yingenus.pocketchinese.domain.entities.dictionarysearch

import com.yingenus.pocketchinese.common.Result

interface Searcher {
    fun find( query : String ) : Result<List<Int>>
}
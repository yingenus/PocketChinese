package com.yingenus.pocketchinese.functions.search

import com.yingenus.pocketchinese.domain.entities.dictionarysearch.ProxySearcherProvider

interface NativeSearchersInitialize {
    fun initializePinyin()
    fun initializeRussian()
}
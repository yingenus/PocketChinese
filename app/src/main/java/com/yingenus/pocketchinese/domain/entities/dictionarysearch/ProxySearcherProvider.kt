package com.yingenus.pocketchinese.domain.entities.dictionarysearch

interface ProxySearcherProvider {
    val russianProxySearcher: ProxySearcher
    val pinyinProxySearcher: ProxySearcher
}
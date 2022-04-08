package com.yingenus.pocketchinese.domain.entities.dictionarysearch

class ProxySearcherProviderImpl : ProxySearcherProvider {
    override val russianProxySearcher: ProxySearcher =
        ProxySearcher()
    override val pinyinProxySearcher: ProxySearcher =
        ProxySearcher()
}
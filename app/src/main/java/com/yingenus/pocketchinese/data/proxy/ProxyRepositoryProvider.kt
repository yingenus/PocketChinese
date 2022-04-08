package com.yingenus.pocketchinese.data.proxy

import com.yingenus.pocketchinese.domain.repository.RadicalsRepository

interface ProxyRepositoryProvider {

    val proxyChinN1Repository : ProxyChinN1Repository
    val proxyChinN2Repository : ProxyChinN2Repository
    val proxyDictionaryItemRepository : ProxyDictionaryItemRepository
    val proxyExampleRepository : ProxyExampleRepository
    val proxyPinUnitWordRepository : ProxyPinUnitWordRepository
    val proxyRusUnitWordRepository : ProxyRusUnitWordRepository
    val proxyRadicalsRepository : ProxyRadicalsRepository
    val proxyToneRepository : ProxyToneRepository
}
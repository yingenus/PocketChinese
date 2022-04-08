package com.yingenus.pocketchinese.data.proxy

class ProxyRepositoryProviderImpl : ProxyRepositoryProvider{

    override val proxyChinN1Repository : ProxyChinN1Repository
        = ProxyChinN1Repository()
    override val proxyChinN2Repository : ProxyChinN2Repository
        = ProxyChinN2Repository()
    override val proxyDictionaryItemRepository : ProxyDictionaryItemRepository
        = ProxyDictionaryItemRepository()
    override val proxyExampleRepository : ProxyExampleRepository
        = ProxyExampleRepository()
    override val proxyPinUnitWordRepository : ProxyPinUnitWordRepository
        = ProxyPinUnitWordRepository()
    override val proxyRusUnitWordRepository : ProxyRusUnitWordRepository
        = ProxyRusUnitWordRepository()
    override val proxyRadicalsRepository : ProxyRadicalsRepository
        = ProxyRadicalsRepository()
    override val proxyToneRepository : ProxyToneRepository
        = ProxyToneRepository()
}
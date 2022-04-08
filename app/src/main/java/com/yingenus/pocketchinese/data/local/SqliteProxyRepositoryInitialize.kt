package com.yingenus.pocketchinese.data.local

import android.content.Context
import com.yingenus.pocketchinese.data.local.db.sqlite.SqliteDatabaseManager
import com.yingenus.pocketchinese.data.proxy.ProxyRepositoryInitialize
import com.yingenus.pocketchinese.data.proxy.ProxyRepositoryProvider

class SqliteProxyRepositoryInitialize(private val provider: ProxyRepositoryProvider,private val dbManager : SqliteDatabaseManager, private val context: Context):
    ProxyRepositoryInitialize{
    override fun initialize() {

        val dictionaryDB = dbManager.getDictionaryDatabase(context)
        val examplesDb = dbManager.getExampleDatabase(context)

        provider.proxyChinN1Repository
            .setRepository(SqliteChnN1SearchRepository(dictionaryDB))
        provider.proxyChinN2Repository
            .setRepository(SqliteChnN2SearchRepository(dictionaryDB))

        val wordRepository = SqliteWordRepository(dictionaryDB)

        provider.proxyDictionaryItemRepository
            .setRepository(wordRepository)
        provider.proxyExampleRepository
            .setRepository(SqliteExampleRepository(examplesDb))
        provider.proxyPinUnitWordRepository
            .setRepository(SqlitePinSearchRepository(dictionaryDB))
        provider.proxyRusUnitWordRepository
            .setRepository(SqliteRusSearchRepository(dictionaryDB))
        provider.proxyRadicalsRepository
            .setRepository(wordRepository)
        provider.proxyToneRepository
            .setRepository(wordRepository)
    }
}
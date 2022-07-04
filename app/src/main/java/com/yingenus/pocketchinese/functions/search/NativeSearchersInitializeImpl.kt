package com.yingenus.pocketchinese.functions.search

import android.content.Context
import com.yingenus.pocketchinese.PocketApplication
import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.domain.entities.dictionarysearch.ProxySearcherProvider

class NativeSearchersInitializeImpl(private val proxySearcherProvider: ProxySearcherProvider, private val context : Context) :
    NativeSearchersInitialize{

    private val indexManager = IndexManagerFactory.create()

    override fun initializePinyin() {

        val pinPath = indexManager.getIndexAbsolutePathLast(Language.PINYIN, context)

        val pinNative : PrefixSearcher

        pinNative = PrefixSearcher()
        pinNative.setLanguage(Language.PINYIN)
        pinNative.init( pinPath)

        proxySearcherProvider.pinyinProxySearcher.initSearcher(NativeSearcher.build(pinNative, Language.PINYIN))
    }

    override fun initializeRussian() {
        val rusPath = indexManager.getIndexAbsolutePathLast(Language.RUSSIAN, context)

        val rusNative : PrefixSearcher

        rusNative = PrefixSearcher()
        rusNative.setLanguage(Language.RUSSIAN)
        rusNative.init(rusPath)

        proxySearcherProvider.russianProxySearcher
            .initSearcher(NativeSearcher.build(rusNative, Language.RUSSIAN))
    }
}
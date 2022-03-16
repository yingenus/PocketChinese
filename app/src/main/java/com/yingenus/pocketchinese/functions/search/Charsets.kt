package com.yingenus.pocketchinese.functions.search

import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.model.LanguageCase
import java.lang.IllegalArgumentException
import java.nio.charset.Charset

fun getCharset( language: Language) : Charset =
        when(language){
            Language.RUSSIAN -> Charset.forName("windows-1251")
            Language.PINYIN -> Charset.forName("US-ASCII")
            Language.CHINESE -> Charset.forName("US-ASCII")
            else -> throw IllegalArgumentException("not support this language : ${language::name}")
        }
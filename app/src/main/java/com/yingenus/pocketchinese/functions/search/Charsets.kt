package com.yingenus.pocketchinese.functions.search

import com.yingenus.pocketchinese.common.Language
import java.lang.IllegalArgumentException
import java.nio.charset.Charset

fun getCharset( language: Language) : Charset =
        when(language){
            Language.RUSSIAN -> Charset.forName("windows-1251")
            Language.PINYIN -> Charset.forName("US-ASCII")
            Language.CHINESE -> Charset.forName("US-ASCII")
            else -> throw IllegalArgumentException("not support this language : ${language::name}")
        }

val prefixLanguages : List<Language> = listOf(Language.RUSSIAN, Language.PINYIN)

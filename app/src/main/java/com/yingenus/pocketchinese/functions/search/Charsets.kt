package com.yingenus.pocketchinese.functions.search

import com.yingenus.pocketchinese.model.LanguageCase
import java.nio.charset.Charset

fun getCharset( languageCase: LanguageCase) : Charset =
        when(languageCase){
            LanguageCase.Trn -> Charset.forName("windows-1251")
            LanguageCase.Pin -> Charset.forName("US-ASCII")
            else -> Charset.forName("US-ASCII")
        }
package com.yingenus.pocketchinese.functions.search

import android.content.Context
import com.yingenus.pocketchinese.common.Language

interface CreateNativeSearcherIterator {
    fun createNativeSearcher(language: Language, context: Context): NativeSearcher
}
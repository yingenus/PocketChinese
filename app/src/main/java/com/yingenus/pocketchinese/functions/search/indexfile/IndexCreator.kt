package com.yingenus.pocketchinese.functions.search.indexfile

import com.yingenus.pocketchinese.functions.search.UnitWordIterator
import java.io.File
import java.nio.charset.Charset

interface IndexCreator {

    fun createIndex(file: File, charset: Charset, iterator : UnitWordIterator)
}
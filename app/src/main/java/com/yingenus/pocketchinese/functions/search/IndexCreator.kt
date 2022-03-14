package com.yingenus.pocketchinese.functions.search

import java.io.File

interface IndexCreator {
    fun createIndex( file : File)
}
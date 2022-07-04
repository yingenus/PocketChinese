package com.yingenus.pocketchinese.functions.search.indexfile

import java.io.File

interface IndexChecker {
    // params file : File - file with prefix search index
    // return : Boolean - true if file checksum and calculated checksum are same
    fun checkIndexFile(file: File): Boolean
}
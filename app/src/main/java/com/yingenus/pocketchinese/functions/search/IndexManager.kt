package com.yingenus.pocketchinese.functions.search

import android.content.Context
import com.yingenus.pocketchinese.common.Language
import java.io.File
import java.nio.file.Path

interface IndexManager {

    // return list of supported languages
    fun supportLanguage(): List<Language>
    // return : Boolean - true if file is exist and checksum is good
    fun checkIndex(language : Language, version : Int, context: Context) : Boolean

    fun getIndexAbsolutePath(language: Language, version : Int, context: Context) : String

    fun getIndexAbsolutePathLast(language: Language, context: Context) : String

    fun createIndex( language: Language, version : Int, unitWordRepository: UnitWordRepository, context: Context)
}
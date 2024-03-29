package com.yingenus.pocketchinese.domain.dto

import java.io.Serializable

class DictionaryItem(
    val id : Int,
    val chinese : String,
    val pinyin : String,
    val translation : Array<String>,
    val generalTag : String,
    val specialTag : Array<String>,
    val chineseOld : String? = null
): Serializable {
}
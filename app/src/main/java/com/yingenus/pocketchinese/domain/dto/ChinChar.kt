package com.yingenus.pocketchinese.domain.dto

class ChinChar(
    val id : Int,
    val chinese : String,
    val pinyin : String,
    val translation : Array<String>,
    val generalTag : String,
    val specialTag : Array<String>
) {
}
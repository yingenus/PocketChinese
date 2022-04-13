package com.yingenus.pocketchinese.domain.dto

class SuggestList(
    val name : String,
    val version : String,
    val words : Int,
    val image : String,
    val tags : List<String>
) {
}
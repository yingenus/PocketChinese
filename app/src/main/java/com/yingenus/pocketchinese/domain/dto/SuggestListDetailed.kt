package com.yingenus.pocketchinese.domain.dto

class SuggestListDetailed(
    val name : String,
    val version : String,
    val words : Int,
    val image : String,
    val description : String,
    val tags : List<String>
) {
}
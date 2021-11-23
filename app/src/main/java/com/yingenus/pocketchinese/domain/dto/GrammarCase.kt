package com.yingenus.pocketchinese.domain.dto

data class GrammarCase(
        val name : String,
        val link : String,
        val title : String,
        val short : String,
        val tags : Array<String>,
        val version : Int,
        val image : String
) {
}
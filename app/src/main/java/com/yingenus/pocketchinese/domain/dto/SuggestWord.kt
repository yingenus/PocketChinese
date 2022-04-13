package com.yingenus.pocketchinese.domain.dto

class SuggestWord(
    val word : String,
    val pinyin : String,
    val translation : String,
    val examples : List<Example>,
    val description : String?
) {
}
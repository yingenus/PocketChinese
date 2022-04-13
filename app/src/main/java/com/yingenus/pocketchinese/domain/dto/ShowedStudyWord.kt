package com.yingenus.pocketchinese.domain.dto

class ShowedStudyWord(
    val id: Long,
    val chinese: String,
    val pinyin: String,
    val translate: String,
    val wordSuccess : Int,
    val recomend: RepeatRecomend
) {
}
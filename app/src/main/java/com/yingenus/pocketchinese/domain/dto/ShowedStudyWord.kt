package com.yingenus.pocketchinese.domain.dto

import java.util.*

class ShowedStudyWord(
    val id: Long,
    val chinese: String,
    val pinyin: String,
    val translate: String,
    val wordSuccess : Int,
    val recomend: RepeatRecomend,
    val createDate: Date
) {
}
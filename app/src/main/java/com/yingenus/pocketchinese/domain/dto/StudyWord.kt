package com.yingenus.pocketchinese.domain.dto

import java.util.*

class StudyWord(
    val id: Long,
    var chinese: String,
    var pinyin: String,
    var translate: String,
    val createDate: Date
    ) {
}
package com.yingenus.pocketchinese.domain.dto

import java.util.*

class ShowedStudyList(
    val id: Long,
    val name: String,
    val words: Int = 0,
    val notifyUser: Boolean,
    val repeat: RepeatRecomend,
    val repeatDate: Date,
    val percentComplete: Int,
    val createDate: Date
) {
}
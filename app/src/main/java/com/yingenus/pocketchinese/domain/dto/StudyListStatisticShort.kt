package com.yingenus.pocketchinese.domain.dto

import java.util.*

class StudyListStatisticShort(
    val words : Int,
    val repeat: RepeatRecomend,
    val percentComplete: Int,
    val nextRepeat: Date?,
    val lastRepeat: Date?
) {
}
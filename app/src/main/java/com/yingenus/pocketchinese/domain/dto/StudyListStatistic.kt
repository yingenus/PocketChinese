package com.yingenus.pocketchinese.domain.dto

import java.util.*

class StudyListStatistic(
    val words : Int,
    val lastRepeat: Date?,
    val nextRepeat: Date?,
    val successChn : Int,
    val successPin : Int,
    val successTrn : Int,
    val repeat: RepeatRecomend,
    val percentComplete: Int,
    val repeatedWords : Int,
    val addedWords : Int
) {
}
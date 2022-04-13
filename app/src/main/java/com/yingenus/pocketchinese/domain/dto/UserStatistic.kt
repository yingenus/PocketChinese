package com.yingenus.pocketchinese.domain.dto

import java.util.*

class UserStatistic(
    val date: Date,
    var added : List<Long>,
    var deleted : List<Long>,
    var repeated : List<Long>,
    var passedChn : Int,
    var passedPin : Int,
    var passedTrn : Int,
    var failedChn : Int,
    var failedPin : Int,
    var failedTrn : Int
) {
}
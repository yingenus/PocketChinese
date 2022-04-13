package com.yingenus.pocketchinese.domain.dto

import java.util.*

class ShowedUserStatistic(
    val added : Int,
    val repeated : Int,
    val passedChn : Int,
    val passedPin : Int,
    val passedTrn : Int,
    val failedChn : Int,
    val failedPin : Int,
    val failedTrn : Int
) {
}
package com.yingenus.pocketchinese.domain.dto

class StudyWordStatistic(
    val wordSuccess : Int, // 0 - 10,
    val chineseSuccessPercent : Int,
    val pinyinSuccessPercent : Int,
    val translationSuccessPercent : Int,
    val recomend: RepeatRecomend
) {
}
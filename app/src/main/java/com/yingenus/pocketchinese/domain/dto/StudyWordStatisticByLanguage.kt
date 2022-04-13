package com.yingenus.pocketchinese.domain.dto

class StudyWordStatisticByLanguage(
    val wordSuccess : Int, // 0 - 10,
    val chineseSuccessPercent : Int,
    val pinyinSuccessPercent : Int,
    val translationSuccessPercent : Int,
    val repeatChinese: RepeatRecomend,
    val repeatPinyin: RepeatRecomend,
    val repeatTranslation: RepeatRecomend,
    val repeatStatusChinese: TrainingStatus,
    val repeatStatusPinyin: TrainingStatus,
    val repeatStatusTranslation: TrainingStatus
) {
}
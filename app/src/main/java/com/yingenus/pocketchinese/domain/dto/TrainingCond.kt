package com.yingenus.pocketchinese.domain.dto

import java.util.*

class TrainingCond(
    val wordID : Long,
    var chineseLevel: KnownLevel,
    var pinyinLevel : KnownLevel,
    var translationLevel : KnownLevel,
    var trainingStatusChinese : TrainingStatus,
    var trainingCountChinese : Int,
    var trainingStatusPinyin : TrainingStatus,
    var trainingCountPinyin : Int,
    var trainingStatusTranslation : TrainingStatus,
    var trainingCountTranslation : Int,
    var trainingDateChinese : Date,
    var trainingDatePinyin : Date,
    var trainingDateTranslation : Date
)
{
    companion object{
        fun creteEmpty(studyWord: StudyWord) : TrainingCond =
            TrainingCond(
                studyWord.id,
                KnownLevel.minLevel,
                KnownLevel.minLevel,
                KnownLevel.minLevel,
                TrainingStatus.SUCCESS,
                0,
                TrainingStatus.SUCCESS,
                0,
                TrainingStatus.SUCCESS,
                0,
                Date(System.currentTimeMillis()),
                Date(System.currentTimeMillis()),
                Date(System.currentTimeMillis())
            )
    }
}
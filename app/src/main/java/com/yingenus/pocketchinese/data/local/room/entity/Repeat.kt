package com.yingenus.pocketchinese.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yingenus.pocketchinese.domain.dto.KnownLevel
import com.yingenus.pocketchinese.domain.dto.TrainingCond
import com.yingenus.pocketchinese.domain.dto.TrainingStatus
import java.util.*

@Entity( tableName = "repeat")
class Repeat(
    @PrimaryKey @ColumnInfo(name = "word_id") val wordID : Long,
    @ColumnInfo(name = "chin_lvl") val chineseLevel: Int,
    @ColumnInfo(name = "pin_lvl") val pinyinLevel : Int,
    @ColumnInfo(name = "trn_lvl") val translationLevel : Int,
    @ColumnInfo(name = "train_status_chn") val trainingStatusChinese : Int,
    @ColumnInfo(name = "train_count_chn") val trainingCountChinese : Int,
    @ColumnInfo(name = "train_status_pin") val trainingStatusPinyin : Int,
    @ColumnInfo(name = "train_count_pin") val trainingCountPinyin : Int,
    @ColumnInfo(name = "train_status_trn") val trainingStatusTranslation : Int,
    @ColumnInfo(name = "train_count_trn") val trainingCountTranslation : Int,
    @ColumnInfo(name = "train_date_chn") val trainingDateChinese : Date,
    @ColumnInfo(name = "train_date_pin") val trainingDatePinyin : Date,
    @ColumnInfo(name = "train_date_trn") val trainingDateTranslation : Date
) {
    companion object{

        private val status : (TrainingStatus) -> Int = {
            when(it){
                TrainingStatus.FILED -> 1
                TrainingStatus.SUCCESS -> 0
            }
        }

        private val trainingStatus : (Int) -> TrainingStatus = {
            when(it){
                0 -> TrainingStatus.SUCCESS
                1 -> TrainingStatus.FILED
                else -> TrainingStatus.FILED
            }
        }

        fun fromTrainingCond( trainingCond : TrainingCond): Repeat =
            Repeat(
                trainingCond.wordID,
                trainingCond.chineseLevel.level,
                trainingCond.pinyinLevel.level,
                trainingCond.translationLevel.level,
                status(trainingCond.trainingStatusChinese),
                trainingCond.trainingCountChinese,
                status(trainingCond.trainingStatusPinyin),
                trainingCond.trainingCountPinyin,
                status(trainingCond.trainingStatusTranslation),
                trainingCond.trainingCountTranslation,
                trainingCond.trainingDateChinese,
                trainingCond.trainingDatePinyin,
                trainingCond.trainingDateTranslation
            )

    }

    fun toTrainingCond():TrainingCond =
        TrainingCond(
            wordID,
            KnownLevel.creteSafe(chineseLevel),
            KnownLevel.creteSafe(pinyinLevel),
            KnownLevel.creteSafe(translationLevel),
            trainingStatus(trainingStatusChinese),
            trainingCountChinese,
            trainingStatus(trainingStatusPinyin),
            trainingCountPinyin,
            trainingStatus(trainingStatusTranslation),
            trainingCountTranslation,
            trainingDateChinese,
            trainingDatePinyin,
            trainingDateTranslation
        )
}
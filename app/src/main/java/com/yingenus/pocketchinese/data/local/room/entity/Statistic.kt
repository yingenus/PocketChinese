package com.yingenus.pocketchinese.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yingenus.pocketchinese.domain.dto.UserStatistic
import java.util.*

@Entity(tableName = "user_statistic")
class Statistic (
    @PrimaryKey @ColumnInfo(name = "date") val date : Date,
    @ColumnInfo(name = "added") val added : String,
    @ColumnInfo(name = "deleted") val deleted : String,
    @ColumnInfo(name = "repeated") val repeated : String,
    @ColumnInfo(name = "chn_passed") val chinesePassed : Int,
    @ColumnInfo(name = "chn_failed") val chineseFailed : Int,
    @ColumnInfo(name = "pin_passed") val pinyinPassed : Int,
    @ColumnInfo(name = "pin_failed") val pinyinFailed : Int,
    @ColumnInfo(name = "trn_passed") val translationPassed : Int,
    @ColumnInfo(name = "trn_failed") val translationFailed : Int
        ){

    companion object{
        fun fromUserStatistic( userStatistic: UserStatistic) : Statistic =
            Statistic(
                date = userStatistic.date,
                added = array2String(userStatistic.added.map { it.toString() }.toTypedArray()),
                deleted = array2String(userStatistic.deleted.map { it.toString() }.toTypedArray()),
                repeated = array2String(userStatistic.repeated.map { it.toString() }.toTypedArray()),
                chinesePassed = userStatistic.passedChn,
                pinyinPassed = userStatistic.passedPin,
                translationPassed = userStatistic.passedTrn,
                chineseFailed = userStatistic.failedChn,
                pinyinFailed = userStatistic.failedPin,
                translationFailed = userStatistic.failedTrn
            )
    }

    fun toUserStatistic() : UserStatistic {

        val addedIds = string2ArrayNoNULL(added).map { it.toLong() }
        val deletedIds = string2ArrayNoNULL(deleted).map { it.toLong() }
        val repeatedIds = string2ArrayNoNULL(repeated).map { it.toLong() }

        return UserStatistic(
            date = date,
            added = addedIds,
            deleted = deletedIds,
            repeated = repeatedIds,
            passedChn =  chinesePassed,
            failedChn =  chineseFailed,
            passedPin = pinyinPassed,
            failedPin = pinyinFailed,
            passedTrn = translationPassed,
            failedTrn = translationFailed
        )
    }

}
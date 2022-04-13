package com.yingenus.pocketchinese.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yingenus.pocketchinese.data.local.room.entity.Statistic
import org.checkerframework.checker.propkey.qual.UnknownPropertyKey
import java.util.*

@Dao
interface StatisticDao {

    @Query("SELECT * FROM user_statistic")
    fun getStatistics(): List<Statistic>

    @Query("SELECT * FROM user_statistic WHERE date >= :date")
    fun getStatistic(date: Date): Statistic?

    @Query("SELECT * FROM user_statistic WHERE date BETWEEN :from AND :to")
    fun getStatistic(from : Date, to : Date): List<Statistic>

    @Query("SELECT * FROM user_statistic WHERE date >= :from")
    fun getStatistic(from : Long): List<Statistic>

    @Insert
    fun creteStatistic(statistic: Statistic)

    @Update
    fun updateStatistic(statistic: Statistic)

}
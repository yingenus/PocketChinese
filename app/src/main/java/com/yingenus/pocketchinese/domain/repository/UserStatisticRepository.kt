package com.yingenus.pocketchinese.domain.repository

import com.yingenus.pocketchinese.domain.dto.TimePeriod
import com.yingenus.pocketchinese.domain.dto.UserStatistic
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import java.util.*

interface UserStatisticRepository {
    fun getStatistic(date: Date): Maybe<UserStatistic>
    fun getStatistics(from : Date, to : Date): Single<List<UserStatistic>>
    fun getStatisticsLast(timePeriod: TimePeriod): Single<List<UserStatistic>>
    fun updateStatistic(userStatistic: UserStatistic) : Completable
    fun createStatistic(userStatistic: UserStatistic): Completable
}
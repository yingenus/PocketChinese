package com.yingenus.pocketchinese.domain.usecase

import com.yingenus.pocketchinese.domain.dto.ShowedUserStatistic
import com.yingenus.pocketchinese.domain.dto.TimePeriod
import com.yingenus.pocketchinese.domain.dto.UserStatistic
import com.yingenus.pocketchinese.domain.entities.studystatictic.UserStatistics
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class UserStatisticUseCaseImpl @Inject constructor(
    private val userStatistics: UserStatistics
) : UserStatisticUseCase {
    override fun getStatistic(): Single<ShowedUserStatistic> {
        return userStatistics.getStatistic(TimePeriod.ONE_MONTH)
    }
}
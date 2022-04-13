package com.yingenus.pocketchinese.domain.usecase

import com.yingenus.pocketchinese.domain.dto.ShowedUserStatistic
import com.yingenus.pocketchinese.domain.dto.UserStatistic
import io.reactivex.rxjava3.core.Single

interface UserStatisticUseCase {
    fun getStatistic(): Single<ShowedUserStatistic>
}
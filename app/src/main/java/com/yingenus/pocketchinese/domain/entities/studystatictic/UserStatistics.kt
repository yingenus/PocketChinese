package com.yingenus.pocketchinese.domain.entities.studystatictic

import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.domain.dto.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.util.*

interface UserStatistics {
    fun getStatistic(interval : TimePeriod): Single<ShowedUserStatistic>
    fun wordAdded(id: Long) : Completable
    fun wordDeleted(id: Long) : Completable
    fun wordTrained(id: Long,trainedResult: TrainedResult, language: Language) : Completable
}
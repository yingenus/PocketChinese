package com.yingenus.pocketchinese.domain.usecase

import io.reactivex.rxjava3.core.Completable

interface ClearWordsRepeatStatistics {
    fun clearStatisticForWords(studyWordId : Long): Completable
    fun clearStatisticForList(studyListId : Long): Completable
}
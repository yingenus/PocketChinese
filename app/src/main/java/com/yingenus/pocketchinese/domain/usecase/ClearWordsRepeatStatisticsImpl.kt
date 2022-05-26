package com.yingenus.pocketchinese.domain.usecase

import com.yingenus.pocketchinese.domain.repository.TrainingRepository
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class ClearWordsRepeatStatisticsImpl @Inject constructor(
    private val trainingRepository: TrainingRepository
)
    : ClearWordsRepeatStatistics {

    override fun clearStatisticForWords(studyWordId: Long): Completable {
        return trainingRepository.deleteTrainingCondForWord(studyWordId)
    }

    override fun clearStatisticForList(studyListId: Long): Completable {
        return trainingRepository.deleteTrainingCondForList(studyListId)
    }
}
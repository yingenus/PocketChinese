package com.yingenus.pocketchinese.domain.repository

import com.yingenus.pocketchinese.data.local.room.entity.StudyWord
import com.yingenus.pocketchinese.domain.dto.TrainingCond
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

interface TrainingRepository {
    fun getTrainingCond( studyWordId: Long) : Maybe<TrainingCond>
    fun getTrainingCondForList( studyListId: Long) : Single<List<TrainingCond>>
    fun creteTrainingCond( trainingCond : TrainingCond) : Completable
    fun deleteTrainingCond( trainingCond : TrainingCond) : Completable
    fun deleteTrainingCondForWord( studyWordId: Long) : Completable
    fun deleteTrainingCondForList( studyListId: Long) : Completable
    fun updateTrainingCond( trainingCond : TrainingCond) : Completable
}
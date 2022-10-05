package com.yingenus.pocketchinese.domain.usecase

import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.domain.dto.StudyWord
import com.yingenus.pocketchinese.domain.dto.TrainingConf
import com.yingenus.pocketchinese.domain.dto.TrainingStatistic
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface TrainingWordsUseCase {
    fun init( trainingConf: TrainingConf) : Completable
    fun getTrainingWords() : Single<List<StudyWord>>
    fun getTrainingStatistic(): Observable<TrainingStatistic>
    fun showAnswer( studyWord: StudyWord) : Single<String>
    fun postAnswer( answer : String, studyWord: StudyWord): Single<Boolean>
    fun isInitialized(): Boolean
}
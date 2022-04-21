package com.yingenus.pocketchinese.domain.usecase

import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.domain.dto.TrainedWords
import com.yingenus.pocketchinese.domain.dto.TrainingConf
import io.reactivex.rxjava3.core.Single

interface TrainedWordsUseCase {
    fun getTrainedWords(language: Language, studyListId : Long): Single<TrainedWords>
}
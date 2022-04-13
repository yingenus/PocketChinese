package com.yingenus.pocketchinese.domain.entities.traning

import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.domain.dto.RepeatType
import com.yingenus.pocketchinese.domain.dto.StudyWord
import com.yingenus.pocketchinese.domain.dto.TrainedResult
import io.reactivex.rxjava3.core.Single

interface Training {
    fun setLanguage(language: Language)
    fun postAnswer( answer : String, word: StudyWord): Single<TrainedResult>
    fun showAnswer(word: StudyWord) : Single<String>
}
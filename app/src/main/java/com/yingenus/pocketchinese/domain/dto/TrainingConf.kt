package com.yingenus.pocketchinese.domain.dto

import com.yingenus.pocketchinese.common.Language
import java.io.Serializable

data class  TrainingConf(
    val language: Language,
    val trainingWords: TrainingWords,
    val studyListId : Long
): Serializable {
    enum class TrainingWords{
        ALL, ONLY_NEED
    }
}
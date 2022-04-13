package com.yingenus.pocketchinese.domain.dto

import com.yingenus.pocketchinese.common.Language

class TrainingConf(
    val language: Language,
    val trainingWords: TrainingWords,
    val studyListId : Long
) {
    enum class TrainingWords{
        ALL, ONLY_NEED
    }
}
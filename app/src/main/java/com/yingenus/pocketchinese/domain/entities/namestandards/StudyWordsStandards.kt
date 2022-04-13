package com.yingenus.pocketchinese.domain.entities.namestandards

import com.yingenus.pocketchinese.common.Language

interface StudyWordsStandards {
    fun isCorrectField(content : String, language: Language): Boolean
    fun toStandards(content : String, language: Language): String
}
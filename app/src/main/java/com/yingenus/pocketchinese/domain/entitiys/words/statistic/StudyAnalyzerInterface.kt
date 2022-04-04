package com.yingenus.pocketchinese.domain.entitiys.words.statistic

import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.StudyWord

interface StudyAnalyzerInterface {
    enum class States{
        NEED_TO_REPEAT,
        FAILED,
        REPEATED

    }

    fun getChnRepeatState(studyWord: StudyWord): States
    fun getPinRepeatState(studyWord: StudyWord): States
    fun getTrnRepeatState(studyWord: StudyWord): States

}
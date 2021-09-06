package com.yingenus.pocketchinese.model.words.statistic

import com.yingenus.pocketchinese.model.database.pocketDB.StudyWord

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
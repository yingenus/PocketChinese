package com.yingenus.pocketchinese.domain.entitiys.words.statistic

import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.StudyWord

class StudyAnalyzer(private val repeatHelper: RepeatHelper): StudyAnalyzerInterface {

    override fun getChnRepeatState(studyWord: StudyWord): StudyAnalyzerInterface.States {
        val howExpired=repeatHelper.howExpired(studyWord.chnLastRepeat,studyWord.chnLevel)

        return if(howExpired== RepeatHelper.Expired.GOOD && studyWord.chnRepState == 0)
            StudyAnalyzerInterface.States.REPEATED
        else if (studyWord.chnRepState > 0)
            StudyAnalyzerInterface.States.FAILED
        else
            StudyAnalyzerInterface.States.NEED_TO_REPEAT
    }
    override fun getPinRepeatState(studyWord: StudyWord): StudyAnalyzerInterface.States {
        val howExpired=repeatHelper.howExpired(studyWord.pinLastRepeat,studyWord.pinLevel)

        return if(howExpired== RepeatHelper.Expired.GOOD && studyWord.pinRepState == 0)
            StudyAnalyzerInterface.States.REPEATED
        else if (studyWord.pinRepState > 0)
            StudyAnalyzerInterface.States.FAILED
        else
            StudyAnalyzerInterface.States.NEED_TO_REPEAT
    }
    override fun getTrnRepeatState(studyWord: StudyWord): StudyAnalyzerInterface.States {
        val howExpired=repeatHelper.howExpired(studyWord.trnLastRepeat,studyWord.trnLevel)

        return if(howExpired== RepeatHelper.Expired.GOOD && studyWord.trnRepState == 0)
            StudyAnalyzerInterface.States.REPEATED
        else if (studyWord.trnRepState > 0)
            StudyAnalyzerInterface.States.FAILED
        else
            StudyAnalyzerInterface.States.NEED_TO_REPEAT
    }

}
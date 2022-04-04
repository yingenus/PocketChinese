package com.yingenus.pocketchinese.domain.entitiys.words

import com.yingenus.pocketchinese.domain.entitiys.words.statistic.FibRepeatHelper
import com.yingenus.pocketchinese.domain.entitiys.words.statistic.StudyAnalyzer
import com.yingenus.pocketchinese.domain.entitiys.words.statistic.StudyAnalyzerInterface
import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.StudyWord
import junit.framework.TestCase
import java.util.*

class StudyAnalyzerTest : TestCase() {
    val studyAnalyzer= StudyAnalyzer(FibRepeatHelper())


    fun testGetChnRepeatState() {
        val lvl=7
        val date= GregorianCalendar()
        var curDate= GregorianCalendar()

        curDate.add(GregorianCalendar.DAY_OF_YEAR,-22)

        val studyWord= StudyWord("test", "test", "test", chnLevel = lvl, pinLevel = lvl, trnLevel = lvl, chnRepeat = 3, chnRepState = 0, pinLastRepeat = curDate.time)
        assertEquals(StudyAnalyzerInterface.States.NEED_TO_REPEAT,studyAnalyzer.getChnRepeatState(studyWord))
    }

    fun testGetPinRepeatState() {
        val lvl=7
        val date= GregorianCalendar()
        var curDate= GregorianCalendar()

        curDate.set(GregorianCalendar.DAY_OF_YEAR,date.get(GregorianCalendar.DAY_OF_YEAR)-19)

        val studyWord= StudyWord("test", "test", "test", chnLevel = lvl, pinLevel = lvl, trnLevel = lvl, pinRepState = 0, pinLastRepeat = curDate.time)
        assertEquals(StudyAnalyzerInterface.States.REPEATED,studyAnalyzer.getPinRepeatState(studyWord))
    }

    fun testGetTrnRepeatState() {val lvl=7
        val date= GregorianCalendar()
        var curDate= GregorianCalendar()

        curDate.set(GregorianCalendar.DAY_OF_YEAR,date.get(GregorianCalendar.DAY_OF_YEAR)-25)

        val studyWord= StudyWord("test", "test", "test", chnLevel = lvl, pinLevel = lvl, trnLevel = lvl, trnRepState = 1, pinLastRepeat = curDate.time)
        assertEquals(StudyAnalyzerInterface.States.FAILED,studyAnalyzer.getTrnRepeatState(studyWord))
    }
}
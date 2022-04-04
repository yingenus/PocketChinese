package com.yingenus.pocketchinese.domain.entitiys.words

import com.yingenus.pocketchinese.domain.entitiys.words.statistic.FibRepeatHelper
import com.yingenus.pocketchinese.domain.entitiys.words.statistic.StudyAnalyzer
import com.yingenus.pocketchinese.domain.entitiys.words.statistic.StudyListRepeatStatistic
import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.StudyWord
import junit.framework.TestCase
import java.util.*

class StudyListRepeatStatisticTest : TestCase() {
    val lvl=7
    val date= GregorianCalendar()
    var curDate= GregorianCalendar()
    var studyWords = mutableMapOf<Int,List<StudyWord>>()
    var studyListRepeatStatistic: StudyListRepeatStatistic

    init {
        curDate.set(GregorianCalendar.DAY_OF_YEAR,date.get(GregorianCalendar.DAY_OF_YEAR)-22)
        val studyWord1= StudyWord("test", "test", "test", chnLevel = lvl, pinLevel = lvl, trnLevel = lvl, chnRepState = -1, pinRepState = 2, trnRepState = 0)
        val studyWord2= StudyWord("test", "test", "test", chnLevel = lvl, pinLevel = lvl, trnLevel = lvl, chnRepState = -1, pinRepState = 2, trnRepState = 0)
        val studyWord3= StudyWord("test", "test", "test", chnLevel = lvl, pinLevel = lvl, trnLevel = lvl, chnRepState = 0, pinRepState = 0, trnRepState = 0)
        var list1 = arrayListOf(studyWord1,studyWord2,studyWord3)


        val studyWord4= StudyWord("test", "test", "test", chnLevel = lvl, pinLevel = lvl, trnLevel = lvl, chnRepState = 0, pinRepState = 0, trnRepState = 1)
        val studyWord5= StudyWord("test", "test", "test", chnLevel = lvl, pinLevel = lvl, trnLevel = lvl, chnRepState = 2, pinRepState = 0, trnRepState = 1)
        val studyWord6= StudyWord("test", "test", "test", chnLevel = lvl, pinLevel = lvl, trnLevel = lvl, chnRepState = -1, pinRepState = -1, trnRepState = 1)
        var list2=arrayListOf(studyWord4,studyWord5,studyWord6)

        studyWords[0] = list1;
        studyWords[1]=  list2

        studyListRepeatStatistic= StudyListRepeatStatistic(studyWords, StudyAnalyzer(FibRepeatHelper()))

    }

    fun testGetChnState() {
        var state= StudyListRepeatStatistic.State(1,3,6)
        var cur=studyListRepeatStatistic.chnState
        assertEquals(state.bed,cur.bed)
        assertEquals(state.good,cur.good)
        assertEquals(state.common,cur.common)
    }

    fun testGetPinState() {
        var state= StudyListRepeatStatistic.State(2,1,6)
        var cur=studyListRepeatStatistic.pinState
        assertEquals(state.bed,cur.bed)
        assertEquals(state.good,cur.good)
        assertEquals(state.common,cur.common)
    }

    fun testGetTrnState() {
        var state= StudyListRepeatStatistic.State(3,0,6)
        var cur=studyListRepeatStatistic.trnState
        assertEquals(state.bed,cur.bed)
        assertEquals(state.good,cur.good)
        assertEquals(state.common,cur.common)
    }

    fun testGetChnBlockState() {
        var state1= StudyListRepeatStatistic.State(0,2,3)
        var state2= StudyListRepeatStatistic.State(1,1,3)

        assertEquals(state1.bed,studyListRepeatStatistic.chnBlockState[0].bed)
        assertEquals(state1.good,studyListRepeatStatistic.chnBlockState[0].good)
        assertEquals(state1.common,studyListRepeatStatistic.chnBlockState[0].common)

        assertEquals(state2.bed,studyListRepeatStatistic.chnBlockState[1].bed)
        assertEquals(state2.good,studyListRepeatStatistic.chnBlockState[1].good)
        assertEquals(state2.common,studyListRepeatStatistic.chnBlockState[1].common)

    }

    fun testGetPinBlockState() {
        var state1= StudyListRepeatStatistic.State(2,0,3)
        var state2= StudyListRepeatStatistic.State(0,1,3)

        assertEquals(state1.bed,studyListRepeatStatistic.pinBlockState[0].bed)
        assertEquals(state1.good,studyListRepeatStatistic.pinBlockState[0].good)
        assertEquals(state1.common,studyListRepeatStatistic.pinBlockState[0].common)

        assertEquals(state2.bed,studyListRepeatStatistic.pinBlockState[1].bed)
        assertEquals(state2.good,studyListRepeatStatistic.pinBlockState[1].good)
        assertEquals(state2.common,studyListRepeatStatistic.pinBlockState[1].common)
    }

    fun testGetTrnBlockState() {
        var state1= StudyListRepeatStatistic.State(0,0,3)
        var state2= StudyListRepeatStatistic.State(3,0,3)

        assertEquals(state1.bed,studyListRepeatStatistic.trnBlockState[0].bed)
        assertEquals(state1.good,studyListRepeatStatistic.trnBlockState[0].good)
        assertEquals(state1.common,studyListRepeatStatistic.trnBlockState[0].common)

        assertEquals(state2.bed,studyListRepeatStatistic.trnBlockState[1].bed)
        assertEquals(state2.good,studyListRepeatStatistic.trnBlockState[1].good)
        assertEquals(state2.common,studyListRepeatStatistic.trnBlockState[1].common)
    }
}
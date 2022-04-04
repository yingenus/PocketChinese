package com.yingenus.pocketchinese.domain.entitiys.words

import com.yingenus.pocketchinese.domain.entitiys.words.statistic.FibRepeatHelper
import junit.framework.TestCase
import java.util.*

class FibRepeatHelperTest : TestCase() {

    val repeatHelper= FibRepeatHelper()

    fun testNextRepeat() {
        val lvl=7
        val date=GregorianCalendar()
        var curDate=GregorianCalendar()
        curDate.add(GregorianCalendar.DAY_OF_YEAR,21)
        assertEquals(curDate.time,repeatHelper.nextRepeat(date.time,lvl))
    }

    fun testHowExpired() {
        val lvl=7
        var curDate=GregorianCalendar()
        curDate.add(GregorianCalendar.DAY_OF_YEAR,-19)
        assertEquals(0,repeatHelper.howExpired(curDate.time,lvl))
        curDate.add(GregorianCalendar.DAY_OF_YEAR,-5)
        assertEquals(1,repeatHelper.howExpired(curDate.time,lvl))
        curDate.add(GregorianCalendar.DAY_OF_YEAR,-2)
        assertEquals(2,repeatHelper.howExpired(curDate.time,lvl))
    }

    fun testNextDay() {
        val exept=arrayOf(0,1,2,3,5,8,13,21,34,55,89)
        val act=arrayOf(repeatHelper.nextDay(0),
                repeatHelper.nextDay(1),
                repeatHelper.nextDay(2),
                repeatHelper.nextDay(3),
                repeatHelper.nextDay(4),
                repeatHelper.nextDay(5),
                repeatHelper.nextDay(6),
                repeatHelper.nextDay(7),
                repeatHelper.nextDay(8),
                repeatHelper.nextDay(9),
                repeatHelper.nextDay(10)
        )
        exept.forEachIndexed { index, i -> assertEquals(i,act[index]) }
    }

}
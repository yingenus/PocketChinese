package com.yingenus.pocketchinese.domain.entitiys.words.statistic

import java.util.*

abstract class RepeatHelper {

    object Expired{
        const val GOOD=0
        const val MEDIUM=1
        const val BED=2
    }

    fun nextRepeat(lastRepeat: Date,lvl: Int): Date {
        val calendar= GregorianCalendar()
        calendar.time= lastRepeat.clone() as Date

        val next = nextDay(lvl)
        val days = next.toInt()
        val minute = ((next - days)*1440).toInt()

        calendar.add(Calendar.DAY_OF_YEAR,days)
        calendar.add(Calendar.MINUTE,minute)
        return calendar.time
    }

    fun howExpired(lastRepeat: Date,lvl: Int):Int{
        val time= GregorianCalendar.getInstance().time.time - lastRepeat.time

        val days = time/(1440*60*1000)

        val expired=days-nextDay(lvl)
        if (expired<0)
            return Expired.GOOD
        if (expired<repeatWindow(lvl))
            return Expired.MEDIUM
        return Expired.BED
    }

    fun canAccept(lastRepeat: Date,lvl: Int): Boolean{
        val time= GregorianCalendar.getInstance().time.time - lastRepeat.time
        val days = time.toDouble()/(1440*60*1000)

        val expired=days-nextDay(lvl)

        return expired > -repeatWindow(lvl)/3
    }

    abstract fun repeatWindow(lvl: Int):Float

    abstract fun nextDay(lvl: Int):Float


}
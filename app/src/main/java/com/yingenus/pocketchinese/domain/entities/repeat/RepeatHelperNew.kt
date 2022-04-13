package com.yingenus.pocketchinese.domain.entities.repeat

import com.yingenus.pocketchinese.domain.dto.KnownLevel
import java.util.*



class RepeatHelperNew(private val repeatScope: RepeatScope) : RepeatHelper {

    override fun nextRepeat(lastRepeat: Date, lvl: KnownLevel) : Date {
        val calendar= GregorianCalendar()
        calendar.time= lastRepeat.clone() as Date

        val next = repeatScope.nextDay(lvl)
        val days = next.toInt()
        val minute = ((next - days)*1440).toInt()

        calendar.add(Calendar.DAY_OF_YEAR,days)
        calendar.add(Calendar.MINUTE,minute)
        return calendar.time
    }

    override fun howExpired(lastRepeat: Date, lvl: KnownLevel) : Expired{
        val time= GregorianCalendar.getInstance().time.time - lastRepeat.time

        val days = time/(1440*60*1000)

        val expired=days-repeatScope.nextDay(lvl)
        if (expired<0)
            return Expired.GOOD
        if (expired<repeatScope.repeatWindow(lvl))
            return Expired.MEDIUM
        return Expired.BED
    }

    override fun canAccept(lastRepeat: Date, lvl: KnownLevel) : Boolean{
        val time= GregorianCalendar.getInstance().time.time - lastRepeat.time
        val days = time.toDouble()/(1440*60*1000)

        val expired=days-repeatScope.nextDay(lvl)

        return expired > -( 10f / 1440 )
    }

}
package com.yingenus.pocketchinese.domain.dto

import java.util.*

enum class TimePeriod {
    ONE_DAY,FIVE_DAYS,FIFTEEN_DAYS, ONE_MONTH, TREE_MONTHS,SIX_MONTHS, ONE_YEAR;

    fun beforeDate( date: Date): Date{
        var time:Long = date.time
        time -= when(this){
            ONE_DAY -> 24L*60*60*1000
            FIVE_DAYS -> 5L*24*60*60*1000
            FIFTEEN_DAYS -> 15L*24*60*60*1000
            ONE_MONTH -> 30L*24*60*60*1000
            TREE_MONTHS -> 3L*30*24*60*60*1000
            SIX_MONTHS -> 6L*30*24*60*60*1000
            ONE_YEAR -> 12L*30*24*60*60*1000

        }
        return Date(time)
    }

}
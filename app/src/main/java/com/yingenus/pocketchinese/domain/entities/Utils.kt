package com.yingenus.pocketchinese.domain.entities

import java.util.*

fun Date.toTimeOrDate(): String{
    return if (this.isToday()){
        val calendar = GregorianCalendar()
        calendar.time = this

        calendar.get(GregorianCalendar.HOUR_OF_DAY).toStringTwoNumbs()+":"+calendar.get(GregorianCalendar.MINUTE).toStringTwoNumbs()
    }else{
        val calendar = GregorianCalendar()
        calendar.time = this

        if (this.isMoreAYaer())
            calendar.get(GregorianCalendar.YEAR).toString()
        else
            calendar.get(GregorianCalendar.DAY_OF_MONTH).toString()+"."+(calendar.get(GregorianCalendar.MONTH)+1).toStringTwoNumbs()
    }
}

fun Date.isMoreAYaer(): Boolean{
    val calendar = GregorianCalendar()
    calendar.time = this
    val now = GregorianCalendar()
    now.time = Date(System.currentTimeMillis())

    return if (now.get(GregorianCalendar.YEAR) == calendar.get(GregorianCalendar.YEAR)){
        false
    }
    else if (now.get(GregorianCalendar.YEAR) == calendar.get(GregorianCalendar.YEAR)-1){
        val month = now.get(GregorianCalendar.MONTH) + 12 - now.get(GregorianCalendar.MONTH)
        month >= 12
    }
    else true
}
fun Date.isToday() : Boolean{
    val calendar = GregorianCalendar()
    calendar.time = this
    val now = GregorianCalendar()
    now.time = Date(System.currentTimeMillis())

    return (
        now.get(GregorianCalendar.YEAR) == calendar.get(GregorianCalendar.YEAR)&&
        now.get(GregorianCalendar.MONTH) == calendar.get(GregorianCalendar.MONTH)&&
        now.get(GregorianCalendar.DAY_OF_MONTH) == calendar.get(GregorianCalendar.DAY_OF_MONTH)
    )
}

private fun Int.toStringTwoNumbs(): String = if(this < 10) "0${this.toString()}" else this.toString()
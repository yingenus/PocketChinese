package com.yingenus.pocketchinese.domain.entitiys

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.domain.dto.RepeatRecomend
import com.yingenus.pocketchinese.presentation.views.userlist.UserListsInterface
import java.util.*

object UtilsVariantParams {

    fun getWords(resources:Resources,words:Int)=resources.getQuantityString(R.plurals.words,words,words)

    fun getSuccess(resources:Resources,lvl:Int)=
            resources.getStringArray(R.array.success_count).get(lvl) ?:""
    fun getColor(resources:Resources,lvl: Int)=
            resources.getIntArray(R.array.success_colors).get(if (lvl==0)0 else lvl-1)


    fun getLstRepeat(resources:Resources,date: Date) =
        getRepeatStr(resources,date,GregorianCalendar.getInstance().time)+" "+resources.getString(R.string.ago_suffix)


    fun getNextRepeat(resources:Resources,date: Date): String{
        val calendar=GregorianCalendar()
        calendar.time=date
        if(GregorianCalendar.getInstance().before(calendar)){
            return resources.getString(R.string.aft_prefix) +" "+ getRepeatStr(resources,GregorianCalendar.getInstance().time,date)
        }
        return resources.getString(R.string.need_repeat)
    }


    private fun getRepeatStr(resources:Resources,date: Date,refDate: Date): String{
        val refCalendar = GregorianCalendar()
        refCalendar.time = refDate

        val calendar=GregorianCalendar()
        calendar.time=date

        val days=refCalendar.get(GregorianCalendar.DAY_OF_YEAR)-
                calendar.get(GregorianCalendar.DAY_OF_YEAR)
        val hours = refCalendar.get(GregorianCalendar.HOUR_OF_DAY)-
                calendar.get(GregorianCalendar.HOUR_OF_DAY)
        val minutes = refCalendar.get(GregorianCalendar.MINUTE) - calendar.get(GregorianCalendar.MINUTE)

        return  if (days ==0 && hours == 0 ) resources.getQuantityString(R.plurals.minuets,minutes,minutes)
                else if (days == 0) resources.getQuantityString(R.plurals.hours,hours,hours)
                else if (days < 14)
                    resources.getQuantityString(R.plurals.days, days, days)
                else if (days in 14..34) {
                    val weeks = days / 7
                    resources.getQuantityString(R.plurals.weeks, weeks, weeks)
                } else if (days in 35..364) {
                    val month = days / 28
                    resources.getQuantityString(R.plurals.month, month, month)
                } else {
                    resources.getString(R.string.more_then_year)
                }
    }


    fun getLstRepeatColor(resources:Resources,howBad:Int)=when(howBad){
        0->getColor(resources,10)
        1->getColor(resources,6)
        2->getColor(resources,1)
        else->getColor(resources,1)
    }
    fun getLstRepeatColor(resources:Resources, expired: UserListsInterface.Expired)=when(expired){
        UserListsInterface.Expired.GOOD->getColor(resources,10)
        UserListsInterface.Expired.MEDIUM->getColor(resources,6)
        UserListsInterface.Expired.BED->getColor(resources,1)
        UserListsInterface.Expired.NON -> Color.TRANSPARENT
        else->getColor(resources,1)
    }

    @SuppressLint("ResourceType")
    fun getLstRepeatColor(view : View, recomed : RepeatRecomend)=when(recomed){
        RepeatRecomend.DONT_NEED->{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                view.resources.getColor(android.R.attr.textColorPrimary,view.context.theme )
            } else {
                view.resources.getColor(android.R.attr.textColorPrimary)
            }
        }
        RepeatRecomend.SHOULD->getColor(view.resources,6)
        RepeatRecomend.NEED->getColor(view.resources,1)
        else->getColor(view.resources,1)
    }

}
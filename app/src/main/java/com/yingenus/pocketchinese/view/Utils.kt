package com.yingenus.pocketchinese.controller

import android.content.Context
import android.util.DisplayMetrics

internal  fun dp2px(dp : Int, context: Context): Int{
    val dm = context.resources.displayMetrics
    return Math.round(dp * (dm.xdpi/ DisplayMetrics.DENSITY_DEFAULT))
}

internal fun getDisplayHeight(context : Context): Int =  try {
        context.display!!.height
    } catch (e : NoSuchMethodError){
        context.resources.displayMetrics.heightPixels
    }

internal fun Throwable.logErrorMes() = cause.toString()+"\n"+suppressed+"\n"+message+"\n"+stackTrace.joinToString(separator = "\n")
package com.yingenus.pocketchinese.controller

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator


object Durations{
    const val ERROR_DURATION = 100L
}

fun vibrate(duration : Long, context: Context){
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
        vibrator.vibrate(VibrationEffect.createOneShot(duration,VibrationEffect.DEFAULT_AMPLITUDE))
    }else{
        vibrator.vibrate(duration)
    }
}
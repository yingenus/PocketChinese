package com.yingenus.pocketchinese.userrepeatnotify

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.SystemClock


private const val notifyInterval : Long = 4 * 60 * 60 * 1000 // 4 hours

fun registerRepeatNotifyAlarm(context : Context){
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = NotifyRepeatWordsService.getIntent(context)

    val pIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 5 * 60 * 1000, notifyInterval, pIntent)

}

fun unregisterNotifyAlarm(context : Context){
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = NotifyRepeatWordsService.getIntent(context)

    val pIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE)

    if (pIntent != null){
        alarmManager.cancel(pIntent)
    }
}
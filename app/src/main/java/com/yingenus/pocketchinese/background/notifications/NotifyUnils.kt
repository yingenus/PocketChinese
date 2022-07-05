package com.yingenus.pocketchinese.background.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.yingenus.pocketchinese.R

object Channels{
    const val REMIND_CHANNEL = "com.yingenus.pocketchinese.notifychannel.remind"
    const val BACKGROUND_WORK_CHANNEL = "com.yingenus.pocketchinese.notifychannel.backgroundworkchannel"
}


@RequiresApi(Build.VERSION_CODES.O)
fun getNotificationsChannels(context: Context): List<NotificationChannel>{

    val channels = mutableListOf<NotificationChannel>()

    val remindName = context.getString(R.string.remind_channel_name)
    val remindDescription = context.getString(R.string.remind_channel_description)
    val remindImportance = NotificationManager.IMPORTANCE_DEFAULT
    val remindChannel = NotificationChannel(Channels.REMIND_CHANNEL, remindName,remindImportance)
        .also { it.description = remindDescription }

    val backgroundName = context.getString(R.string.background_work_name)
    val backgroundDescription = context.getString(R.string.background_work_description)
    val backgroundImportance = NotificationManager.IMPORTANCE_DEFAULT
    val backgroundChannel = NotificationChannel(Channels.BACKGROUND_WORK_CHANNEL, backgroundName, backgroundImportance)
        .also { it.description = backgroundDescription }

    channels.add(remindChannel)
    channels.add(backgroundChannel)

    return channels
}


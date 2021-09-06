package com.yingenus.pocketchinese.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.yingenus.pocketchinese.R

object Channels{
    const val REMIND_CHANNEL = "com.example.pocketchinese.notifychannel.remind"
}


@RequiresApi(Build.VERSION_CODES.O)
fun getNotificationsChannels(context: Context): List<NotificationChannel>{

    val channels = mutableListOf<NotificationChannel>()

    val remindName = context.getString(R.string.remind_channel_name)
    val remindDescription = context.getString(R.string.remind_channel_description)
    val remindImportance = NotificationManager.IMPORTANCE_DEFAULT
    val remindChannel = NotificationChannel(Channels.REMIND_CHANNEL, remindName,remindImportance)
        .also { it.description = remindDescription }

    channels.add(remindChannel)

    return channels
}


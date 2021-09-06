package com.yingenus.pocketchinese.userrepeatnotify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DeviceRebootReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent!!.action == Intent.ACTION_BOOT_COMPLETED)
            registerRepeatNotifyAlarm(context!!)
    }

}
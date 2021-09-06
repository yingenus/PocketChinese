package com.yingenus.pocketchinese.userrepeatnotify

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder

class NotifyRepeatWordsService  : Service(){

    companion object{
        fun getIntent(context : Context) = Intent(context, NotifyRepeatWordsService::class.java)
    }


    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")

    }

}
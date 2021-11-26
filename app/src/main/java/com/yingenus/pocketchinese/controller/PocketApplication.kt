package com.yingenus.pocketchinese.controller

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.*
import com.yingenus.pocketchinese.model.database.CopierDBs
import com.yingenus.pocketchinese.notifications.getNotificationsChannels
import com.yingenus.pocketchinese.workers.CheckRepeatableWordsWorker
import com.yingenus.pocketchinese.workers.DbCleanWorker
import java.io.IOException
import java.util.concurrent.TimeUnit

class PocketApplication: Application(), Configuration.Provider {
    private var isApplicationStared = false

    companion object{

        private var pocketApplication : PocketApplication? = null


        fun postStartActivity(fromLaunch : Boolean){
            pocketApplication?.postStartActivity(fromLaunch)
        }

        fun updateNotificationStatus(){
            pocketApplication?.initNotifications()
        }

    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()
    }

    override fun onCreate() {
        pocketApplication = this
        super.onCreate()
        setup()

        if (Settings.isNightModeOn(applicationContext)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

    }

    private fun setup(){
        copyDBs()
        setupNotifyChannels()
        initWorks()
        initNotifications()
    }

    private fun setupNotifyChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channels = getNotificationsChannels(applicationContext)
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            notificationManager.createNotificationChannels(channels)
        }
    }

    private fun initWorks(){
        val wm = WorkManager.getInstance(applicationContext)

        val cleanConstrains = Constraints.Builder()
            .setRequiresBatteryNotLow(true).build()

        val cleanWork =
            PeriodicWorkRequestBuilder<DbCleanWorker>(3, TimeUnit.DAYS)
                .setInitialDelay(3,TimeUnit.DAYS)
                .setConstraints(cleanConstrains)
                .build()
        /*
        val checkWork = PeriodicWorkRequestBuilder<CheckRepeatableWordsWorker>(4,TimeUnit.HOURS)
                .setInitialDelay(3,TimeUnit.HOURS)
                .build()

        wm.enqueueUniquePeriodicWork("check_repeatable_words", ExistingPeriodicWorkPolicy.KEEP,checkWork)
         */
        wm.enqueueUniquePeriodicWork("clean_db",ExistingPeriodicWorkPolicy.KEEP,cleanWork)
    }

    private fun initNotifications(){
        val shouldShow = Settings.shouldShowNotifications(applicationContext)

        val wm = WorkManager.getInstance(applicationContext)
        if (!shouldShow){
            wm.cancelUniqueWork("check_repeatable_words")
        }else{
            val checkWork = PeriodicWorkRequestBuilder<CheckRepeatableWordsWorker>(4,TimeUnit.HOURS)
                    .setInitialDelay(3,TimeUnit.HOURS)
                    .build()
            wm.enqueueUniquePeriodicWork("check_repeatable_words", ExistingPeriodicWorkPolicy.KEEP,checkWork)
        }
    }

    private fun postStartActivity(fromLaunch : Boolean){
        if (isApplicationStared) return
        else isApplicationStared = true

        if (fromLaunch) {
            if(Settings.shouldShowNotifications(applicationContext)) {
                val wm = WorkManager.getInstance(applicationContext)
                val checkWork = OneTimeWorkRequestBuilder<CheckRepeatableWordsWorker>()
                        .setInitialDelay(0, TimeUnit.SECONDS).build()
                wm.enqueueUniqueWork("check_repeatable_words_on_start", ExistingWorkPolicy.REPLACE, checkWork)
            }
        }
    }

    private fun copyDBs(){
        //copyDB(2, "examplesDB.db")
        //copyDB(2, "dictionaryDB.db")
    }

    private fun copyDB(version : Int, name : String){

        val copier = CopierDBs(name)

        if (!copier.isExist(applicationContext, version)){
            Log.i("PocketApplication","copy db: $name, version: $version")
            try {
                copier.copyDB(applicationContext)
            }catch (ioe : IOException){
                Log.w("PocketApplication", "ioe wile copy $name")
            }
        }else{
            Log.i("PocketApplication","db: $name exist and last version")
        }

    }




}
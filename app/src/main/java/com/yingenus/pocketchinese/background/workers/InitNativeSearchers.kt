package com.yingenus.pocketchinese.background.workers

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.yingenus.pocketchinese.PocketApplication
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.background.notifications.Channels

class InitNativeSearchers (
    context: Context,
    workerParameters: WorkerParameters
) : Worker(context , workerParameters) {

    private var notifyBuilder : NotificationCompat.Builder? = null

    @SuppressLint("RestrictedApi")
    override fun doWork(): Result {
        try {

            initNotification()

            val initializer = PocketApplication.getSearcherInitialize()

            super.setProgressAsync(
                Data.Builder()
                    .putAll(mapOf(
                        progressStageName to applicationContext.getString(R.string.InitNativeSearchers_info_start_extract_rus_index),
                        progressPercents to 0
                    )).build())

            updateNotification(0, applicationContext.getString(R.string.InitNativeSearchers_info_start_extract_rus_index))

            initializer.initializeRussian()

            super.setProgressAsync(
                Data.Builder()
                    .putAll(mapOf(
                        progressStageName to applicationContext.getString(R.string.InitNativeSearchers_info_finish_extract_rus_index),
                        progressPercents to 50
                    )).build())

            updateNotification(50, applicationContext.getString(R.string.InitNativeSearchers_info_finish_extract_rus_index))

            super.setProgressAsync(
                Data.Builder()
                    .putAll(mapOf(
                        progressStageName to applicationContext.getString(R.string.InitNativeSearchers_info_start_extract_pin_index),
                        progressPercents to 50
                    )).build())

            updateNotification(50, applicationContext.getString(R.string.InitNativeSearchers_info_start_extract_pin_index))

            initializer.initializePinyin()

            super.setProgressAsync(
                Data.Builder()
                    .putAll(mapOf(
                        progressStageName to applicationContext.getString(R.string.InitNativeSearchers_info_finish_extract_pin_index),
                        progressPercents to 100
                    )).build())

            updateNotification(100, applicationContext.getString(R.string.InitNativeSearchers_info_finish_extract_pin_index))
            cancelNotification()

            return Result.success()
        }catch (e : Exception){
            return Result.failure()
        }
    }


    private fun initNotification(){
        notifyBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            NotificationCompat.Builder(applicationContext, Channels.REMIND_CHANNEL)
        else
            NotificationCompat.Builder(applicationContext)

        notifyBuilder!!.setSmallIcon(R.drawable.ic_app_notify)
            .setContentTitle(applicationContext.getString(R.string.InitNativeSearchers_info_start))
            .setProgress(100, 0 ,false)

        val notification : Notification = notifyBuilder!!.build()

        NotificationManagerCompat
            .from(applicationContext)
            .notify(R.id.background_work_notification, notification)

        val foregroundInfo = ForegroundInfo(R.id.background_work_notification, notification)
        setForegroundAsync(foregroundInfo)

    }

    private fun updateNotification(progress : Int, message : String){
        notifyBuilder
            ?.setProgress(100, minOf(progress, 100), false)
            ?.setContentTitle(message)

        notifyBuilder?.let {
            NotificationManagerCompat.from(applicationContext).notify(R.id.background_work_notification, it.build())
        }
    }

    private fun cancelNotification(){
        NotificationManagerCompat.from(applicationContext).cancel(R.id.background_work_notification)
    }

}
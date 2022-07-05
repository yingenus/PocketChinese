package com.yingenus.pocketchinese.background.workers

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.yingenus.pocketchinese.PocketApplication
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.background.notifications.Channels

class InitDatabaseRepositorys (
    context: Context,
    workerParameters: WorkerParameters
) : Worker(context , workerParameters) {


    @SuppressLint("RestrictedApi")
    override fun doWork(): Result {
        try {
            val notifyBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                NotificationCompat.Builder(applicationContext, Channels.REMIND_CHANNEL)
            else
                NotificationCompat.Builder(applicationContext)

            notifyBuilder.setSmallIcon(R.drawable.ic_app_notify)
                .setContentTitle(applicationContext.getString(R.string.InitDatabase_info_start))

            val notification : Notification = notifyBuilder.build()

            NotificationManagerCompat
                .from(applicationContext)
                .notify(R.id.background_work_notification, notification)

            val foregroundInfo = ForegroundInfo(R.id.background_work_notification, notification)
            setForegroundAsync(foregroundInfo)

            PocketApplication.getDatabaseInitialize().initialize()

            NotificationManagerCompat
                .from(applicationContext)
                .cancel(R.id.background_work_notification)

            return Result.success()
        }catch (e : Exception){
            return Result.failure()
        }
    }
}
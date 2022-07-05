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
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.background.notifications.Channels
import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.data.local.SqlitePinSearchRepository
import com.yingenus.pocketchinese.data.local.SqliteRusSearchRepository
import com.yingenus.pocketchinese.data.local.db.sqlite.InAssetsSqliteDatabaseManager
import com.yingenus.pocketchinese.data.local.db.sqlite.SqliteDatabaseManager
import com.yingenus.pocketchinese.data.local.sqlite.DictionaryDBHelper
import com.yingenus.pocketchinese.functions.search.IndexManager
import com.yingenus.pocketchinese.functions.search.IndexManagerFactory
import java.lang.Exception

class CreateIndexWorker(
    context: Context,
    workerParameters: WorkerParameters
) : Worker(context , workerParameters) {

    private var notifyBuilder : NotificationCompat.Builder? = null

    @SuppressLint("RestrictedApi")
    override fun doWork(): Result {
        try {
            val indexManager : IndexManager = IndexManagerFactory.create()
            val databaseManager : SqliteDatabaseManager = InAssetsSqliteDatabaseManager()

            initNotification()

            val helper : DictionaryDBHelper = databaseManager.getDictionaryDatabase(applicationContext)

            val version = databaseManager.getDatabaseVersion(DictionaryDBHelper::class.java.name,applicationContext)

            val rusRepo = SqliteRusSearchRepository(helper)
            val pinRepo = SqlitePinSearchRepository(helper)


            if (!indexManager.checkIndex(Language.PINYIN, version, applicationContext)){
                super.setProgressAsync(
                    Data.Builder()
                        .putAll(mapOf(
                            progressStageName to applicationContext.getString(R.string.CreateIndexWorker_info_start_crete_pin_index),
                            progressPercents to 0
                        )).build())

                updateNotification(0,applicationContext.getString(R.string.CreateIndexWorker_info_start_crete_pin_index))

                indexManager.createIndex(Language.PINYIN,version,pinRepo,applicationContext)

                super.setProgressAsync(
                    Data.Builder()
                        .putAll(mapOf(
                            progressStageName to applicationContext.getString(R.string.CreateIndexWorker_info_finish_crete_pin_index),
                            progressPercents to 50
                        )).build())

                updateNotification(50, applicationContext.getString(R.string.CreateIndexWorker_info_finish_crete_pin_index))
            }



            if (!indexManager.checkIndex(Language.RUSSIAN, version, applicationContext)){
                super.setProgressAsync(
                    Data.Builder()
                        .putAll(mapOf(
                            progressStageName to applicationContext.getString(R.string.CreateIndexWorker_info_start_crete_rus_index),
                            progressPercents to 50
                        )).build())

                updateNotification(50,applicationContext.getString(R.string.CreateIndexWorker_info_start_crete_rus_index))

                indexManager.createIndex(Language.RUSSIAN,version,rusRepo,applicationContext)
                super.setProgressAsync(
                    Data.Builder()
                        .putAll(mapOf(
                            progressStageName to applicationContext.getString(R.string.CreateIndexWorker_info_finish_crete_rus_index),
                            progressPercents to 50
                        )).build())

                updateNotification(100, applicationContext.getString(R.string.CreateIndexWorker_info_finish_crete_rus_index))
            }

            databaseManager.close()

            cancelNotification()

            return Result.success()
        }catch ( exp : Exception){
            return Result.failure()
        }
    }

    private fun initNotification(){
        notifyBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            NotificationCompat.Builder(applicationContext, Channels.REMIND_CHANNEL)
        else
            NotificationCompat.Builder(applicationContext)

        notifyBuilder!!.setSmallIcon(R.drawable.ic_app_notify)
            .setContentTitle(applicationContext.getString(R.string.CreateIndexWorker_info_start))
            .setProgress(100, 0 ,false)

        val notification : Notification  = notifyBuilder!!.build()

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
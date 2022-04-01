package com.yingenus.pocketchinese.workers

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.data.local.db.sqlite.InAssetsSqliteDatabaseManager
import com.yingenus.pocketchinese.data.local.db.sqlite.SqliteDatabaseManager
import com.yingenus.pocketchinese.data.local.sqlite.DictionaryDBHelper
import com.yingenus.pocketchinese.data.local.sqlite.ExamplesDBHelper

class CopyDBWorker(
    context: Context,
    workerParameters: WorkerParameters
) : Worker(context , workerParameters) {

    @SuppressLint("RestrictedApi")
    override fun doWork(): Result {
        try {

            val databaseManager : SqliteDatabaseManager = InAssetsSqliteDatabaseManager()

            super.setProgressAsync(Data(
                mapOf(
                    progressStageName to applicationContext.getString(R.string.CopyDbWorker_info_start_copy),
                    progressPercents to 0
                )))

            if (!databaseManager.isDatabaseExist(ExamplesDBHelper::class.java.name,applicationContext)
                ||!databaseManager.isActualVersion(ExamplesDBHelper::class.java.name,applicationContext))
            {
                databaseManager.updateDatabase(ExamplesDBHelper::class.java.name,applicationContext)
            }

            super.setProgressAsync(Data(
                mapOf(
                    progressStageName to applicationContext.getString(R.string.CopyDbWorker_info_start_copy),
                    progressPercents to 50
                )))

            if (!databaseManager.isDatabaseExist(DictionaryDBHelper::class.java.name,applicationContext)
                ||!databaseManager.isActualVersion(DictionaryDBHelper::class.java.name,applicationContext))
            {
                databaseManager.updateDatabase(DictionaryDBHelper::class.java.name,applicationContext)
            }
            super.setProgressAsync(Data(
                mapOf(
                    progressStageName to applicationContext.getString(R.string.CopyDbWorker_info_finish_copy),
                    progressPercents to 100
                )))

            return Result.success()
        }catch ( e : Exception){
            return Result.failure()
        }
    }
}
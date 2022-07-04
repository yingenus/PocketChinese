package com.yingenus.pocketchinese.background.workers

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.yingenus.pocketchinese.R
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

    @SuppressLint("RestrictedApi")
    override fun doWork(): Result {
        try {
            val indexManager : IndexManager = IndexManagerFactory.create()
            val databaseManager : SqliteDatabaseManager = InAssetsSqliteDatabaseManager()

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

                indexManager.createIndex(Language.PINYIN,version,pinRepo,applicationContext)

                super.setProgressAsync(
                    Data.Builder()
                        .putAll(mapOf(
                            progressStageName to applicationContext.getString(R.string.CreateIndexWorker_info_finish_crete_pin_index),
                            progressPercents to 50
                        )).build())
            }

            if (!indexManager.checkIndex(Language.RUSSIAN, version, applicationContext)){
                super.setProgressAsync(
                    Data.Builder()
                        .putAll(mapOf(
                            progressStageName to applicationContext.getString(R.string.CreateIndexWorker_info_start_crete_rus_index),
                            progressPercents to 50
                        )).build())



                indexManager.createIndex(Language.RUSSIAN,version,rusRepo,applicationContext)
                super.setProgressAsync(
                    Data.Builder()
                        .putAll(mapOf(
                            progressStageName to applicationContext.getString(R.string.CreateIndexWorker_info_finish_crete_rus_index),
                            progressPercents to 50
                        )).build())
            }

            databaseManager.close()

            return Result.success()
        }catch ( exp : Exception){
            return Result.failure()
        }
    }





}
package com.yingenus.pocketchinese.background.workers

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.yingenus.pocketchinese.PocketApplication
import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.di.ServiceLocator
import com.yingenus.pocketchinese.domain.entities.dictionarysearch.Searcher
import com.yingenus.pocketchinese.functions.search.IndexManagerImpl
import com.yingenus.pocketchinese.functions.search.NativeSearcher
import com.yingenus.pocketchinese.functions.search.PrefixSearcher

class initDatabaseRepositorys (
    context: Context,
    workerParameters: WorkerParameters
) : Worker(context , workerParameters) {


    @SuppressLint("RestrictedApi")
    override fun doWork(): Result {
        try {
            ServiceLocator.initAllProxy(applicationContext)
            return Result.success()
        }catch (e : Exception){
            return Result.failure()
        }
    }
}
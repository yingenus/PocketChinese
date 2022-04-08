package com.yingenus.pocketchinese.background.workers

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.yingenus.pocketchinese.PocketApplication

class InitDatabaseRepositorys (
    context: Context,
    workerParameters: WorkerParameters
) : Worker(context , workerParameters) {


    @SuppressLint("RestrictedApi")
    override fun doWork(): Result {
        try {
            PocketApplication.getDatabaseInitialize().initialize()
            return Result.success()
        }catch (e : Exception){
            return Result.failure()
        }
    }
}
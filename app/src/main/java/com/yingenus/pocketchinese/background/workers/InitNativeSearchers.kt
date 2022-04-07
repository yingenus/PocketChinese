package com.yingenus.pocketchinese.background.workers

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.yingenus.pocketchinese.PocketApplication
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.functions.search.IndexManagerImpl
import com.yingenus.pocketchinese.functions.search.NativeSearcher
import com.yingenus.pocketchinese.functions.search.PrefixSearcher
import com.yingenus.pocketchinese.workers.progressPercents
import com.yingenus.pocketchinese.workers.progressStageName

class InitNativeSearchers (
    context: Context,
    workerParameters: WorkerParameters
) : Worker(context , workerParameters) {


    @SuppressLint("RestrictedApi")
    override fun doWork(): Result {
        try {

            val indexManager = IndexManagerImpl()

            val rusPath = indexManager.getIndexAbsolutePathLast(Language.RUSSIAN, applicationContext)
            val pinPath = indexManager.getIndexAbsolutePathLast(Language.PINYIN, applicationContext)

            val pinNative : PrefixSearcher
            val rusNative : PrefixSearcher

            super.setProgressAsync(
                Data.Builder()
                    .putAll(mapOf(
                        progressStageName to applicationContext.getString(R.string.InitNativeSearchers_info_start_extract_rus_index),
                        progressPercents to 0
                    )).build())

            rusNative = PrefixSearcher()
            rusNative.setLanguage(Language.RUSSIAN)
            rusNative.init(rusPath)

            super.setProgressAsync(
                Data.Builder()
                    .putAll(mapOf(
                        progressStageName to applicationContext.getString(R.string.InitNativeSearchers_info_finish_extract_rus_index),
                        progressPercents to 50
                    )).build())

            super.setProgressAsync(
                Data.Builder()
                    .putAll(mapOf(
                        progressStageName to applicationContext.getString(R.string.InitNativeSearchers_info_start_extract_pin_index),
                        progressPercents to 50
                    )).build())

            pinNative = PrefixSearcher()
            pinNative.setLanguage(Language.PINYIN)
            pinNative.init( pinPath)

            super.setProgressAsync(
                Data.Builder()
                    .putAll(mapOf(
                        progressStageName to applicationContext.getString(R.string.InitNativeSearchers_info_finish_extract_pin_index),
                        progressPercents to 100
                    )).build())

            PocketApplication.setPinSearcher(NativeSearcher.build(pinNative,Language.PINYIN))
            PocketApplication.setRusSearcher(NativeSearcher.build(rusNative,Language.RUSSIAN))

            return Result.success()
        }catch (e : Exception){
            return Result.failure()
        }
    }
}
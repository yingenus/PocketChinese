package com.yingenus.pocketchinese.background.workers

import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.PocketApplication
import com.yingenus.pocketchinese.Settings
import com.yingenus.pocketchinese.presentation.views.userlist.RepeatableUserListsActivity
import com.yingenus.pocketchinese.logErrorMes
import com.yingenus.pocketchinese.domain.dto.RepeatType
import com.yingenus.pocketchinese.domain.entities.repeat.FibRepeatHelper
import com.yingenus.pocketchinese.domain.entities.repeat.RepeatHelperOld
import com.yingenus.pocketchinese.background.notifications.Channels
import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.domain.dto.ShowedStudyList
import com.yingenus.pocketchinese.domain.dto.TrainedWords
import com.yingenus.pocketchinese.domain.usecase.ModifyStudyListUseCase
import com.yingenus.pocketchinese.domain.usecase.StudyListInfoUseCase
import com.yingenus.pocketchinese.domain.usecase.TrainedWordsUseCase
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

import java.sql.SQLException
import javax.inject.Inject

class CheckRepeatableWordsWorker( context: Context, workerParameters: WorkerParameters) : Worker(context , workerParameters){

    companion object{


        private fun notifyBuilder(context : Context, word : Int, list : Int):  NotificationCompat.Builder{
            val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                NotificationCompat.Builder(context,Channels.REMIND_CHANNEL)
            else
                NotificationCompat.Builder(context)

            builder.setSmallIcon(R.drawable.ic_app_notify)
            builder.setContentTitle(context.getString(R.string.remind_notify_title))

            val words = context.resources.getQuantityString(R.plurals.words_dc,word,word)
            val lists = context.resources.getQuantityString(R.plurals.lists,list,list)

            builder.setContentText(context.getString(R.string.remind_notify_text, words, lists))
            builder.priority = NotificationCompat.PRIORITY_DEFAULT

            val pIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                PendingIntent.getActivity(context,0,RepeatableUserListsActivity.getIntent(context),PendingIntent.FLAG_IMMUTABLE)
            else
                PendingIntent.getActivity(context,0, RepeatableUserListsActivity.getIntent(context), PendingIntent.FLAG_UPDATE_CURRENT)

            builder.setContentIntent(pIntent)
            builder.setAutoCancel(true)

            return builder

        }
    }

    @Inject
    lateinit var trainedWords: TrainedWordsUseCase
    @Inject
    lateinit var studyListInfoUseCase: StudyListInfoUseCase

    override fun doWork(): Result{
        Log.d("CheckRepeatable","Worker start")

        PocketApplication.setupApplication()
        PocketApplication.getAppComponent().injectCheckRepeatableWordsWorker(this)

        try {

            studyListInfoUseCase
                .getAllStudyLists()
                .flatMapObservable{
                    Observable.fromIterable(it)
                }.flatMap { studyList ->
                    Single.zip(
                        trainedWords.getTrainedWords(Language.CHINESE,studyList.id),
                        trainedWords.getTrainedWords(Language.PINYIN,studyList.id),
                        trainedWords.getTrainedWords(Language.RUSSIAN,studyList.id)
                    ){ chn, pin, trn ->
                        val chnNeeded = chn.filed + chn.repeatable
                        val pinNeeded = pin.filed + pin.repeatable
                        val trnNeeded = trn.filed + trn.repeatable
                        maxOf(chnNeeded,pinNeeded,trnNeeded)
                    }
                        .map { studyList to it }
                        .toObservable()
                }.collect({ mutableListOf<Pair<ShowedStudyList, Int>>()},{ list, item ->
                    if(item.second != 0) list.add(item)
                })
                .subscribe({result ->
                    if (result.isNotEmpty())
                        with(NotificationManagerCompat.from(applicationContext)){
                            val words = result.map { it.second }.reduce { acc, pair -> acc + pair }
                            val lists = result.size
                            notify(R.id.repeat_words_notification, notifyBuilder(applicationContext,words,lists).build())
                        }
                },{})

            return Result.success()

        }catch (e : SQLException){
            Log.e("CheckRepeatable", e.cause?.logErrorMes()?:"error with no msg")
            return Result.failure()
        }
    }

}
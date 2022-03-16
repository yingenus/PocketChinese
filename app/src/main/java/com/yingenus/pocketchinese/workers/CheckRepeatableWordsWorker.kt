package com.yingenus.pocketchinese.workers

import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.Settings
import com.yingenus.pocketchinese.controller.activity.RepeatableUserListsActivity
import com.yingenus.pocketchinese.logErrorMes
import com.yingenus.pocketchinese.model.RepeatType
import com.yingenus.pocketchinese.model.database.PocketDBOpenManger
import com.yingenus.pocketchinese.model.database.pocketDB.*
import com.yingenus.pocketchinese.model.words.statistic.FibRepeatHelper
import com.yingenus.pocketchinese.model.words.statistic.RepeatHelper
import com.yingenus.pocketchinese.notifications.Channels
import java.sql.SQLException

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

            val pIntent = PendingIntent.getActivity(context,0, RepeatableUserListsActivity.getIntent(context), PendingIntent.FLAG_UPDATE_CURRENT)

            builder.setContentIntent(pIntent)
            builder.setAutoCancel(true)

            return builder

        }
    }

    override fun doWork(): Result{
        Log.d("CheckRepeatable","Worker start")
        try {
            val checker = RepeatableChecker(applicationContext, Settings.getRepeatType(applicationContext))
            val expired = checker.haveExpired()
            Log.d("CheckRepeatable","Have Expired: "+expired)
            if (! expired) return Result.success()

            val repeatable = checker.repealable!!

            checker.finish()

            with(NotificationManagerCompat.from(applicationContext)){
                val words = repeatable.map { it.second.size }.reduce { acc, i -> acc+i }
                val lists = repeatable.size
                notify(R.id.repeat_words_notification, notifyBuilder(applicationContext,words,lists).build())
            }

            return Result.success()

        }catch (e : SQLException){
            Log.e("CheckRepeatable", e.cause?.logErrorMes()?:"error with no msg")
            return Result.failure()
        }
    }

    private class RepeatableChecker(context: Context, val repeatType: RepeatType){

        private val repeatHelper = FibRepeatHelper()

        private val listDAO : StudyListDAO
        private val wordsDAO : StudyWordDAO

        var repealable : List<Pair<StudyList,List<StudyWord>>>? = emptyList()
            get(){
                if (field.isNullOrEmpty()){
                    field = getExpired()
                }
                return field
            }

        init {
            val sqlDb = PocketDBOpenManger.getHelper(context).writableDatabase

            listDAO = StudyListDAO(sqlDb)
            wordsDAO = StudyWordDAO(sqlDb)
        }

        fun haveExpired(): Boolean{
            return !repealable.isNullOrEmpty()
        }

        fun finish(){
            listDAO.finish()
            wordsDAO.finish()

            PocketDBOpenManger.releaseHelper()
        }

        private fun getExpired(): List<Pair<StudyList,List<StudyWord>>>{
            val lists = listDAO.getAll()?.filter { it.notifyUser }?: emptyList()

            val expired = lists?.map { list ->
                val words = wordsDAO.getAllIn(list)?.map { it.second }?: emptyList()

                val expired = getExpiredWords(words)

                Pair(list,expired)
            }

            return expired.filter { !it.second.isNullOrEmpty() }
        }

        private fun getExpiredWords(words : List<StudyWord>): List<StudyWord>{
            return words.filter {word ->  howExpired(word) != RepeatHelper.Expired.GOOD }
        }

        private fun howExpired(word: StudyWord): Int{
            val pinE = if (repeatType.ignorePIN) RepeatHelper.Expired.GOOD else
                repeatHelper.howExpired(word.pinLastRepeat, word.pinLevel)
            val trnE = if (repeatType.ignoreTRN) RepeatHelper.Expired.GOOD else
                repeatHelper.howExpired(word.trnLastRepeat, word.trnLevel)
            val chnE = if (repeatType.ignoreCHN) RepeatHelper.Expired.MEDIUM else
                repeatHelper.howExpired(word.chnLastRepeat, word.chnLevel)

            return Math.max(pinE,Math.max(trnE,chnE))
        }

    }

}
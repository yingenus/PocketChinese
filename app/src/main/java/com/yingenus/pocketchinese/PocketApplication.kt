package com.yingenus.pocketchinese.controller

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.*
import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.data.local.RoomPinSearchRepository
import com.yingenus.pocketchinese.data.local.RoomRusSearchRepository
import com.yingenus.pocketchinese.data.local.SqlitePinSearchRepository
import com.yingenus.pocketchinese.data.local.SqliteRusSearchRepository
import com.yingenus.pocketchinese.data.local.db.DictionaryDatabaseVersion
import com.yingenus.pocketchinese.data.local.db.room.RoomDatabaseManager
import com.yingenus.pocketchinese.data.local.db.sqlite.SqliteDatabaseManager
import com.yingenus.pocketchinese.di.ServiceLocator
import com.yingenus.pocketchinese.domain.entities.dictionarysearch.Searcher
import com.yingenus.pocketchinese.functions.search.CreateNativeSearcherIterator
import com.yingenus.pocketchinese.functions.search.CreatePrefixSearchIteratorImpl
import com.yingenus.pocketchinese.model.database.CopierDBs
import com.yingenus.pocketchinese.notifications.getNotificationsChannels
import com.yingenus.pocketchinese.workers.CheckRepeatableWordsWorker
import com.yingenus.pocketchinese.workers.DbCleanWorker
import java.io.IOException
import java.util.concurrent.TimeUnit

class PocketApplication: Application(), Configuration.Provider {
    private var isApplicationStared = false
    private var isApplicationSetUp = false

    private val setupChain =
        SetUpNotifyChannels(SetUpWorkers(SetUpNotifications(SetUpCreteNativeSearcher(null))))

    companion object{

        private var pocketApplication : PocketApplication? = null

        lateinit var rusSearcher: Searcher;
        lateinit var pinSearcher: Searcher;


        fun postStartActivity(fromLaunch : Boolean){
            pocketApplication?.postStartActivity(fromLaunch)
        }

        fun setupApplication(){
            pocketApplication?.setup()
        }

        fun updateNotificationStatus(){
            if(pocketApplication != null) SetUpNotifications(null).setup(pocketApplication!!)
        }

    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()
    }

    override fun onCreate() {
        pocketApplication = this
        super.onCreate()
        //setup()

        if (Settings.isNightModeOn(applicationContext)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

    }

    override fun onTerminate() {
        super.onTerminate()
    }

    private fun setup(){
        if (!isApplicationSetUp) {
            setupChain.setup(this)
        }
    }

    private fun postStartActivity(fromLaunch : Boolean){
        if (isApplicationStared) return
        else isApplicationStared = true

        if (fromLaunch) {
            if(Settings.shouldShowNotifications(applicationContext)) {
                val wm = WorkManager.getInstance(applicationContext)
                val checkWork = OneTimeWorkRequestBuilder<CheckRepeatableWordsWorker>()
                        .setInitialDelay(0, TimeUnit.SECONDS).build()
                wm.enqueueUniqueWork("check_repeatable_words_on_start", ExistingWorkPolicy.REPLACE, checkWork)
            }
        }
    }

    private fun copyDBs(){
        //copyDB(2, "examplesDB.db")
        //copyDB(2, "dictionaryDB.db")
    }

    private fun copyDB(version : Int, name : String){

        val copier = CopierDBs(name)

        if (!copier.isExist(applicationContext, version)){
            Log.i("PocketApplication","copy db: $name, version: $version")
            try {
                copier.copyDB(applicationContext)
            }catch (ioe : IOException){
                Log.w("PocketApplication", "ioe wile copy $name")
            }
        }else{
            Log.i("PocketApplication","db: $name exist and last version")
        }

    }
}

abstract class SetupChain(private val next : SetupChain?){
    fun setup(context: PocketApplication){
        setupActions(context)
        next?.setup(context)
    }
    abstract fun setupActions(context: PocketApplication)
}
abstract class TerminateChain(private val next : TerminateChain?){
    fun terminate(context: PocketApplication){
        terminateActions(context)
        next?.terminate(context)
    }
    abstract fun terminateActions(context: PocketApplication)
}

class SetUpNotifyChannels( next : SetupChain?): SetupChain(next){
    override fun setupActions(context: PocketApplication) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channels = getNotificationsChannels(context)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            notificationManager.createNotificationChannels(channels)
        }
    }
}

class SetUpWorkers( next : SetupChain?): SetupChain(next){
    override fun setupActions(context: PocketApplication) {
        val wm = WorkManager.getInstance(context)

        val cleanConstrains = Constraints.Builder()
            .setRequiresBatteryNotLow(true).build()

        val cleanWork =
            PeriodicWorkRequestBuilder<DbCleanWorker>(3, TimeUnit.DAYS)
                .setInitialDelay(3,TimeUnit.DAYS)
                .setConstraints(cleanConstrains)
                .build()
        /*
        val checkWork = PeriodicWorkRequestBuilder<CheckRepeatableWordsWorker>(4,TimeUnit.HOURS)
                .setInitialDelay(3,TimeUnit.HOURS)
                .build()

        wm.enqueueUniquePeriodicWork("check_repeatable_words", ExistingPeriodicWorkPolicy.KEEP,checkWork)
         */
        wm.enqueueUniquePeriodicWork("clean_db",ExistingPeriodicWorkPolicy.KEEP,cleanWork)
    }
}

class SetUpNotifications( next : SetupChain?): SetupChain(next){
    override fun setupActions(context: PocketApplication) {
        val shouldShow = Settings.shouldShowNotifications(context)

        val wm = WorkManager.getInstance(context)
        if (!shouldShow){
            wm.cancelUniqueWork("check_repeatable_words")
        }else{
            val checkWork = PeriodicWorkRequestBuilder<CheckRepeatableWordsWorker>(4,TimeUnit.HOURS)
                .setInitialDelay(3,TimeUnit.HOURS)
                .build()
            wm.enqueueUniquePeriodicWork("check_repeatable_words", ExistingPeriodicWorkPolicy.KEEP,checkWork)
        }
    }
}

class SetUpCreteNativeSearcher( next : SetupChain?): SetupChain(next){
    override fun setupActions(context: PocketApplication) {
        val pinRepo = ServiceLocator.get<SqlitePinSearchRepository>(context, SqlitePinSearchRepository::class.java.name)
        val rusRepo = ServiceLocator.get<SqliteRusSearchRepository>(context,SqliteRusSearchRepository::class.java.name)

        val iterator : CreateNativeSearcherIterator = CreatePrefixSearchIteratorImpl(
            (ServiceLocator.get(context, SqliteDatabaseManager::class.java.name) as DictionaryDatabaseVersion).getVersion(context),
            Language.RUSSIAN to rusRepo,
            Language.PINYIN to pinRepo
        )

        PocketApplication.rusSearcher = iterator.createNativeSearcher(Language.RUSSIAN, context)
        PocketApplication.pinSearcher = iterator.createNativeSearcher(Language.PINYIN, context)

    }
}

class TerminateDatabases(){

}
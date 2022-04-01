package com.yingenus.pocketchinese

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentActivity
import androidx.work.*
import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.data.local.*
import com.yingenus.pocketchinese.data.local.db.sqlite.InAssetsSqliteDatabaseManager
import com.yingenus.pocketchinese.data.local.db.sqlite.SqliteDatabaseManager
import com.yingenus.pocketchinese.data.local.sqlite.DictionaryDBHelper
import com.yingenus.pocketchinese.data.local.sqlite.ExamplesDBHelper
import com.yingenus.pocketchinese.data.proxy.*
import com.yingenus.pocketchinese.di.ServiceLocator
import com.yingenus.pocketchinese.domain.dto.VariantWord
import com.yingenus.pocketchinese.domain.entities.dictionarysearch.ProxySearcher
import com.yingenus.pocketchinese.domain.entities.dictionarysearch.Searcher
import com.yingenus.pocketchinese.domain.repository.DictionaryItemRepository
import com.yingenus.pocketchinese.domain.repository.ExampleRepository
import com.yingenus.pocketchinese.domain.repository.RadicalsRepository
import com.yingenus.pocketchinese.domain.repository.ToneRepository
import com.yingenus.pocketchinese.domain.repository.search.NgramRepository
import com.yingenus.pocketchinese.domain.repository.search.UnitWordRepository
import com.yingenus.pocketchinese.functions.search.*
import com.yingenus.pocketchinese.notifications.getNotificationsChannels
import com.yingenus.pocketchinese.presentation.dialogs.LoadingDialog
import com.yingenus.pocketchinese.workers.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit

class PocketApplication: Application(), Configuration.Provider {
    private var isApplicationStared = false
    private var isApplicationSetUp = false
    private var isSearchersInited = false

    private val setupChain =
        SetUpNotifyChannels(SetUpDbIndexInstallation(SetUpWorkers(SetUpNotifications(null))))
    //private val setUpNativeSearchChain = SetUpCreteNativeSearcher(null)

    val initWorkersId = mutableListOf<UUID>()

    companion object{

        const val INIT_APP_UNIQUE_WORK = "database_init"

        private var pocketApplication : PocketApplication? = null

        private val _rusSearcher: ProxySearcher = ProxySearcher()
        val rusSearcher : Searcher
            get() = _rusSearcher

        private val _pinSearcher: ProxySearcher = ProxySearcher()
        val pinSearcher : Searcher
            get() = _pinSearcher

        fun setRusSearcher( searcher: Searcher){
            _rusSearcher.initSearcher(searcher)
        }

        fun setPinSearcher( searcher: Searcher){
            _pinSearcher.initSearcher(searcher)
        }

        fun postStartActivity(activity: FragmentActivity, fromLaunch : Boolean): Completable{
            val initComplete =  pocketApplication!!.postStartActivity(activity, fromLaunch)
            return initComplete
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



    @SuppressLint("EnqueueWork")
    private fun setup(){
        if (!isApplicationSetUp) {
            isApplicationSetUp =true
            setupChain.setup(this)
        }
    }

    private fun postStartActivity(activity: FragmentActivity, fromLaunch : Boolean): Completable{

        if (isApplicationStared) Completable.create { it.onComplete() }

        if (fromLaunch) {
            if(Settings.shouldShowNotifications(applicationContext)) {
                val wm = WorkManager.getInstance(applicationContext)
                val checkWork = OneTimeWorkRequestBuilder<CheckRepeatableWordsWorker>()
                        .setInitialDelay(0, TimeUnit.SECONDS).build()
                wm.enqueueUniqueWork("check_repeatable_words_on_start", ExistingWorkPolicy.REPLACE, checkWork)
            }
        }

        val publishers: List<PublishSubject<Data>> = initWorkersId.map { PublishSubject.create() }


        val uniqueLive = WorkManager.getInstance(this).getWorkInfosForUniqueWorkLiveData(INIT_APP_UNIQUE_WORK)

        uniqueLive.observe(activity){ infos : List<WorkInfo> ->
            infos.forEach {  info ->
                val index = initWorkersId.indexOf(info.id)
                val publisher = publishers.getOrNull(index) ?: return@observe

                when(info.state){
                    WorkInfo.State.SUCCEEDED -> publisher.onComplete()
                    WorkInfo.State.FAILED -> publisher.onError(Throwable("smh happened"))
                    WorkInfo.State.RUNNING -> {
                        publisher.onNext(info.progress)
                    }
                    else -> {
                        //To do nothing yet
                    }
                }
            }
        }

        val progressObservables = publishers.mapIndexed { index, publishSubject ->

            val del = publishers.size
            val from = (100 / publishers.size) * index

            publishSubject
                .map {
                val process = it.getInt(progressPercents, -1)
                val state = it.getString(progressStageName)?:""
                process to state
            }
                .filter { it.first != -1 }
                .map { (from + it.first/del) to it.second }

        }

        val commonObservable = if (progressObservables.size == 1){
            progressObservables.first()
        }else{
            Observable.concat(progressObservables)
        }
            .doOnComplete {
                uniqueLive.removeObservers(activity)
                isApplicationStared = true
            }
            .publish()

        commonObservable.connect()

        val loadingDialog = LoadingDialog()
        loadingDialog.registerObserver(commonObservable)
        val fm = activity.supportFragmentManager
        loadingDialog.show(fm,"loading_dialog")


        return commonObservable.ignoreElements()
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

class SetUpDbIndexInstallation( next : SetupChain?): SetupChain(next){
    override fun setupActions(context: PocketApplication) {
        val wm = WorkManager.getInstance(context)

        if (isLongInit(context)){
            val copyDBWorker = OneTimeWorkRequestBuilder<CopyDBWorker>().build()
            val createIndexWorker = OneTimeWorkRequestBuilder<CreateIndexWorker>().build()
            val initPrefixWorker = OneTimeWorkRequestBuilder<InitNativeSearchers>().build()
            val initDataBaseRepositorys = OneTimeWorkRequestBuilder<initDatabaseRepositorys>().build()

            context.initWorkersId.add(copyDBWorker.id)
            context.initWorkersId.add(createIndexWorker.id)
            context.initWorkersId.add(initPrefixWorker.id)
            context.initWorkersId.add(initDataBaseRepositorys.id)

            wm.beginUniqueWork(PocketApplication.INIT_APP_UNIQUE_WORK, ExistingWorkPolicy.KEEP, copyDBWorker)
                .then(createIndexWorker)
                .then(initPrefixWorker)
                .then(initDataBaseRepositorys)
                .enqueue()
        }else{
            SetUpIndexes(SetUpDataBaseRepositorys(null)).setup(context)
        }
    }

    private fun isLongInit(context: PocketApplication): Boolean{

        val databaseManager : SqliteDatabaseManager = InAssetsSqliteDatabaseManager()
        return true
        return ! (databaseManager.isActualVersion(ExamplesDBHelper::class.java.name, context) &&
        databaseManager.isActualVersion(DictionaryDBHelper::class.java.name, context))
    }
}

class SetUpIndexes(next : SetupChain?): SetupChain(next){
    override fun setupActions(context: PocketApplication) {
        val indexManager = IndexManagerImpl()

        val rusPath = indexManager.getIndexAbsolutePathLast(Language.RUSSIAN, context)
        val pinPath = indexManager.getIndexAbsolutePathLast(Language.PINYIN, context)

        val pinNative : PrefixSearcher
        val rusNative : PrefixSearcher

        rusNative = PrefixSearcher()
        rusNative.setLanguage(Language.RUSSIAN)
        rusNative.init(rusPath)

        pinNative = PrefixSearcher()
        pinNative.setLanguage(Language.PINYIN)
        pinNative.init( pinPath)

        PocketApplication.setPinSearcher(NativeSearcher.build(pinNative,Language.PINYIN))
        PocketApplication.setRusSearcher(NativeSearcher.build(rusNative,Language.RUSSIAN))
    }
}

class SetUpDataBaseRepositorys(next : SetupChain?): SetupChain(next){
    override fun setupActions(context: PocketApplication) {
        ServiceLocator.initAllProxy(context)
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

class TerminateDatabases(){

}
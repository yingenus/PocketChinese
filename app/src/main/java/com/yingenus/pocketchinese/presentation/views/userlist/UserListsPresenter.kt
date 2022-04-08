package com.yingenus.pocketchinese.presentation.views.userlist

import android.content.Context
import android.util.Log
import com.yingenus.pocketchinese.Settings
import com.yingenus.pocketchinese.controller.logErrorMes
import com.yingenus.pocketchinese.domain.entitiys.database.PocketDBOpenManger
import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.ConnectionDAO
import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.StudyListDAO
import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.StudyWordDAO
import com.yingenus.pocketchinese.domain.entitiys.words.statistic.FibRepeatHelper
import com.yingenus.pocketchinese.domain.entitiys.words.statistic.RepeatHelper
import com.yingenus.pocketchinese.domain.entitiys.words.statistic.StudyListAnalyzer
import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.StudyList
import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.StudyWord
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class UserListsPresenter(val view : UserListsInterface){

    private lateinit var analyzer: StudyListAnalyzer
    private val repeatHelper = FibRepeatHelper()

    private lateinit var listDAO: StudyListDAO
    private lateinit var connectionDAO: ConnectionDAO
    private lateinit var wordDAO: StudyWordDAO

    private var isFirstStart = true

    @Volatile
    private var showedStudyLists : MutableList<UserListsInterface.StudyListInfo> = mutableListOf()

    fun onCreate(context: Context){
        val sqlDb = PocketDBOpenManger.getHelper(context).writableDatabase

        listDAO = StudyListDAO(sqlDb)
        connectionDAO = ConnectionDAO(sqlDb)
        wordDAO = StudyWordDAO(sqlDb)

        analyzer = StudyListAnalyzer(Settings.getRepeatType(context),repeatHelper)

        view.setStudyLists(showedStudyLists)
        updateLists()
    }

    fun onResume(){
        if (isFirstStart){
            isFirstStart = false
            return
        }
        updateLists()
    }

    fun onDestroy(){
        listDAO.finish()
        connectionDAO.finish()
        wordDAO.finish()
        PocketDBOpenManger.releaseHelper()
    }

    fun removeStudyList(item : StudyList){
        try {
            listDAO.remove(item)
            updateLists()
        } catch (e: Exception) {
            Log.e("Remove StudyList Error", e.message!!)
        }
    }

    fun renameStudyList(item: StudyList){
        //view.updateStudyLists(listOf(showedStudyLists.find { it.studyList.uuid == item.uuid }!!))
        updateLists()
    }

    private fun updateLists(){
        Observable.defer {
                    val lists = listDAO.getAll()
                    if (lists.isEmpty()) Observable.just(null)
                    else Observable.fromIterable( lists)
                }
                .subscribeOn(Schedulers.io())
                .map { studyList -> if (studyList == null) null else Pair(studyList,wordDAO.getAllIn(studyList.uuid)?.map { it.second }) }
                .map { pair -> if (pair == null) null else getStatistic(pair.first,pair.second) }
                .observeOn(AndroidSchedulers.mainThread())
                .collect({ mutableListOf<UserListsInterface.StudyListInfo>()}, { acc, item -> if (item != null) acc.add(item)})
                .subscribe({onNext ->
                        if (onNext.isEmpty()) view.showStartView()
                        else view.setStudyLists(onNext)},
                        {onError ->
                            view.showStartView()
                            Log.e("USPresenter",onError.logErrorMes())
                        }
                )

    }

    private fun checkAndUpdate(info : UserListsInterface.StudyListInfo){
        val existInfo = showedStudyLists.find { it.studyList.uuid == info.studyList.uuid }
        if (existInfo == null){
            showedStudyLists.add(info)
            //view.addStudyList(info)
        }else if (existInfo.expered != info.expered || existInfo.success != info.success || existInfo.wordsCount != info.wordsCount || existInfo.lastRepeat != info.lastRepeat){
            val position = showedStudyLists.indexOf(existInfo)
            showedStudyLists.removeAt(position)
            showedStudyLists.add(position,info)
            //view.updateStudyLists(listOf(info))
        }
    }

    private fun getStatistic(studyList: StudyList, words : List<StudyWord>?): UserListsInterface.StudyListInfo{
        if (words.isNullOrEmpty()){
            return UserListsInterface.StudyListInfo(studyList,studyList.updateDate,0, UserListsInterface.Expired.NON, 0)
        }
        val stat = analyzer.getState(words)
        return UserListsInterface.StudyListInfo(studyList,stat.lastRepeat,stat.success, howExpired(stat.worstExpired),stat.words)
    }

    private fun howExpired(expired : Int)=
            when(expired){
            RepeatHelper.Expired.GOOD -> UserListsInterface.Expired.GOOD
            RepeatHelper.Expired.MEDIUM-> UserListsInterface.Expired.MEDIUM
            RepeatHelper.Expired.BED -> UserListsInterface.Expired.BED
            else -> UserListsInterface.Expired.NON
        }

}
package com.yingenus.pocketchinese.presenters

import android.content.Context
import android.util.Log
import com.yingenus.pocketchinese.Settings
import com.yingenus.pocketchinese.controller.fragment.StudyListInterface
import com.yingenus.pocketchinese.controller.logErrorMes
import com.yingenus.pocketchinese.model.RepeatType
import com.yingenus.pocketchinese.model.database.PocketDBOpenManger
import com.yingenus.pocketchinese.model.database.pocketDB.Connection
import com.yingenus.pocketchinese.model.database.pocketDB.ConnectionDAO
import com.yingenus.pocketchinese.model.database.pocketDB.StudyListDAO
import com.yingenus.pocketchinese.model.database.pocketDB.StudyWordDAO
import com.yingenus.pocketchinese.model.words.statistic.FibRepeatHelper
import com.yingenus.pocketchinese.model.words.statistic.StudyListAnalyzer
import com.yingenus.pocketchinese.model.database.pocketDB.StudyList
import com.yingenus.pocketchinese.model.database.pocketDB.StudyWord
import java.lang.NullPointerException
import java.sql.SQLException
import java.util.*

class StudyListPresenter(val view : StudyListInterface) {

    private lateinit var listDAO : StudyListDAO
    private lateinit var connectionDAO : ConnectionDAO
    private lateinit var wordDAO : StudyWordDAO

    private lateinit var repeatType : RepeatType
    private val analyzer : StudyListAnalyzer by lazy { StudyListAnalyzer(repeatType,repeatHelper) }
    private val repeatHelper = FibRepeatHelper()

    private  var studyList : StudyList?  = null
    private var studyWords : Map<Int, List<StudyWord>>? = null
    private lateinit var studyListUUID: UUID

    private var isFirstStart = true

    fun onCreate(context: Context, studyListUUID: UUID){

        val sqlDb = PocketDBOpenManger.getHelper(context).writableDatabase

        listDAO = StudyListDAO(sqlDb)
        connectionDAO = ConnectionDAO(sqlDb)
        wordDAO = StudyWordDAO(sqlDb)

        repeatType = Settings.getRepeatType(context)

        this.studyListUUID = studyListUUID

        init()
    }

    fun onResume(){
        if (isFirstStart){
            isFirstStart = false
            return
        }
        loadWords()
        updateWords()
        updateStatistic()
    }

    fun onDestroy(){
        listDAO.finish()
        connectionDAO.finish()
        wordDAO.finish()
        PocketDBOpenManger.releaseHelper()
    }

    fun removeWords(words : List<StudyWord>){
        for( word in words){
            connectionDAO.remove(Connection(studyListUUID,word.uuid,0))
        }
        loadWords()
        updateWords()
    }

    fun moveWords(words : List<StudyWord>, newBlock : Int){

        val newList = mutableMapOf<Int,MutableList<StudyWord>>()

        studyWords?.keys?.forEach { key: Int ->
            val transferList = studyWords!![key]!!.toMutableList()
            transferList.removeAll(words)
            newList[key] = transferList
        }
        if (!newList.containsKey(newBlock)){
            newList[newBlock] = mutableListOf()
        }
        newList[newBlock]!!.addAll(words)

        var block = 0

        for (key in newList.keys.toList().sorted()){
            val newBlock = block + 1
            if (!newList[key].isNullOrEmpty()){
                block = newBlock
            }
            if (key != newBlock){
                newList[newBlock] = newList[key]!!
                newList.remove(key)
            }
        }

        newList.keys.forEach { key : Int ->
            if (!studyWords!!.containsKey(key)){
                newList[key]?.forEach{word ->
                    connectionDAO.update(Connection(studyListUUID,word.uuid,key))
                }
            }else{
                val oldList = studyWords!![key]!!

                newList[key]?.forEach{word ->
                    if (!oldList.contains(word))
                        connectionDAO.update(Connection(studyListUUID,word.uuid,key))
                }
            }

        }

        loadWords()
        updateWords()
    }

    fun deleteClicked(words : List<StudyWord>?){
        if(!words.isNullOrEmpty())
            view.suggestDeleteWords(words)
    }

    fun setNotify(canNotify : Boolean){
        if (studyList == null) return
        if (studyList!!.notifyUser != canNotify){
            studyList!!.notifyUser = canNotify
            listDAO.update(studyList!!)
        }
    }

    fun moveClicked(words : List<StudyWord>?){
        if(!words.isNullOrEmpty())
            view.suggestMoveWord(words,studyWords?.keys?.maxOrNull()?:1)
    }

    fun editClicked(word : StudyWord?){
        if(word != null)
            view.editWord(word,studyListUUID)
    }

    fun trainClicked(){
        if (!studyWords.isNullOrEmpty())
            view.startTrain(studyListUUID)
    }
    fun addWordClicked(){
        view.addWord(studyListUUID)
    }

    private fun init(){
        loadListInfo()
        loadWords()
        updateWords()
        updateStatistic()

        if(studyList != null){
            view.setName(studyList!!.name)
            view.setNotify(studyList!!.notifyUser)
        }

    }

    private fun loadListInfo(){
        try {
            studyList = listDAO.get(studyListUUID)
        }catch (e : SQLException){
            Log.e("StudyListPresenter", e.cause?.logErrorMes() ?: " ")
        }
    }

    private fun loadWords(){
        try {
            studyWords = wordDAO.getAllInSorted(studyList!!)
        }catch (e : SQLException){
            Log.e("StudyListPresenter", e.cause?.logErrorMes() ?: " ")
            studyWords = null
        }catch (e : NullPointerException){
            Log.i("StudyListPresenter", "no Study List")
            studyWords = null
        }
    }

    private fun updateWords(){
        if (!studyWords.isNullOrEmpty()) {
            view.setStudyWords(studyWords!!)
            view.setWordsCount(studyWords!!.size)
        }
        else {
            view.setStudyWords(emptyMap())
            view.setWordsCount(0)
        }
    }

    private fun updateStatistic(){
        if (studyWords.isNullOrEmpty()){
            setEmptyStat()
            return
        }

        val stat = analyzer.getPercentState(studyWords!!.values.flatten())

        view.setPinStat(stat.successPin)
        view.setChnStat(stat.successChn)
        view.setTrnStat(stat.successTrn)
        view.setWordsCount(stat.words)
        view.lastRepeat(stat.lastRepeat)
        view.nextRepeat(nextRepeat(studyWords!!.values.flatten(),repeatType))
    }

    private fun setEmptyStat(){
        view.setPinStat(-1)
        view.setChnStat(-1)
        view.setTrnStat(-1)
        view.setWordsCount(-1)
        view.lastRepeat( null )
        view.nextRepeat( null )
    }


    private fun nextRepeat( studyWords : List<StudyWord>, repeatType: RepeatType): Date{
        return studyWords.map { nextRepeat(it, repeatType) }.reduce { acc, date -> if (acc.before(date)) acc else date }
    }

    private fun nextRepeat(studyWord: StudyWord, repeatType: RepeatType) : Date{
        val dates = mutableListOf<Date>()

        if (!repeatType.ignoreCHN)
            dates.add(repeatHelper.nextRepeat(studyWord.chnLastRepeat,studyWord.chnLevel))
        if (!repeatType.ignorePIN)
            dates.add(repeatHelper.nextRepeat(studyWord.pinLastRepeat,studyWord.pinLevel))
        if (!repeatType.ignoreTRN)
            dates.add(repeatHelper.nextRepeat(studyWord.trnLastRepeat,studyWord.trnLevel))

        return dates.reduce { acc, date -> if (acc.before(date)) acc else date }

    }


}
package com.yingenus.pocketchinese.model.words

import com.yingenus.pocketchinese.model.LanguageCase
import com.yingenus.pocketchinese.model.database.pocketDB.*
import com.yingenus.pocketchinese.model.randomize
import com.yingenus.pocketchinese.model.words.statistic.RepeatHelper
import com.yingenus.pocketchinese.model.words.statistic.StudyAnalyzer
import com.yingenus.pocketchinese.model.words.statistic.StudyAnalyzerInterface
import java.util.*
import kotlin.math.max

class RepeatManager(
        dbHelper : PocketBaseHelper,
        val  studyListUUID: UUID,
        val repeatHelper: RepeatHelper,
        val language: LanguageCase = LanguageCase.Chin,
        val  block : Int = 0 ) {

    private val studyAnalyzer : StudyAnalyzerInterface = StudyAnalyzer(repeatHelper)
    private val answerChecker : AnswerChecker  = when(language){
        LanguageCase.Pin -> PinAnswerChecker()
        LanguageCase.Trn -> TrnAnswerChecker()
        LanguageCase.Chin -> ChnAnswerChecker()
    }

    private val listDAO : StudyListDAO
    private val connectionDAO : ConnectionDAO
    private val wordDAO : StudyWordDAO

    private val studyList : StudyList
    private val words : List<StudyWord>

    private val repeatedWords : MutableList<StudyWord> = mutableListOf()
    private val filedWords : MutableList<StudyWord> = mutableListOf()
    private val editWords : MutableList<StudyWord> = mutableListOf()

    var passed: Int = 0
        private set
    val failed: Int
        get() = filedWords.size
    val total: Int

    private companion object{
        const val lowerLvl = 3
        const val notIncrease = 2

        fun shouldIncreaseLvl(state : Int) : Boolean = state < notIncrease
        fun shouldLowerLvl(state : Int) : Boolean = state >= lowerLvl
    }

    init {

        val sqlDb = dbHelper.writableDatabase

        listDAO = StudyListDAO(sqlDb)
        connectionDAO = ConnectionDAO(sqlDb)
        wordDAO = StudyWordDAO(sqlDb)

        studyList = listDAO.get(studyListUUID)!!

        val allWords = wordDAO.getAllInSorted(studyListUUID)
        words = if (block == 0)
                allWords.values.flatMap { it }
            else
                allWords[block] ?: listOf()


        val repWords = mutableListOf<StudyWord>()

        for(word in words){
            val state = word.wordState()
            if (state != StudyAnalyzerInterface.States.REPEATED)
                repWords.add(word)
            if (state == StudyAnalyzerInterface.States.FAILED)
                filedWords.add(word)
        }

        if (repWords.isEmpty())
            repWords.addAll(words)

        repeatedWords.addAll(repWords.randomize())

        total = repeatedWords.size
    }

    fun getRepeatQueueSnapshot(): List<StudyWord> = repeatedWords.toList()

    fun skipWord( word: StudyWord){
        if (repeatedWords.contains(word)){
            repeatedWords.remove(word)
            repeatedWords.add(word)
        }
    }

    fun discloseWord( word : StudyWord){
        if (repeatedWords.contains(word)){
            wordFailed(word)
        }
    }

    fun checkWord( word : StudyWord, answer : String): Boolean{
        if (repeatedWords.contains(word)){
            return if (answerChecker.check(word,answer)){
                wordPassed(word)
                true
            }else{
                wordFailed(word)
                false
            }

        }
        return  false
    }

    private fun wordPassed(word : StudyWord){
        val state = word.repState()
        word.wasTryRepeat()
        if (repeatHelper.canAccept(word.lastRepeat(),word.lvl()) && !filedWords.contains(word)){
            if (shouldIncreaseLvl(state)){
                word.setLvl(word.lvl()+1)
            }else if( shouldLowerLvl(state)){
                val  lvl = word.lvl()
                val dec = word.lvl() - max(1 , lvl - notIncrease)
                word.setLvl(lvl - dec)
            }
            studyList.lastRepeat = Date()
            //repeatedWords.remove(word)
            if (filedWords.contains(word)){
                filedWords.remove(word)
            }
            word.setRepState(0)
            word.updateLastRepeat()
        }
        else if (filedWords.contains(word)){
            if( shouldLowerLvl(state)){
                val  lvl = word.lvl()
                val dec = word.lvl() - max(1 , lvl - notIncrease)
                word.setLvl(lvl - dec)
            }
            studyList.lastRepeat = Date()
            //repeatedWords.remove(word)
            if (filedWords.contains(word)){
                filedWords.remove(word)
            }
            word.setRepState(0)
            word.updateLastRepeat()
        }
        repeatedWords.remove(word)
        passed += 1
    }

    private fun wordFailed(word : StudyWord){
        word.setRepState(word.repState()+1)
        word.wasTryRepeat()
        repeatedWords.remove(word)
        repeatedWords.add(word)
        if(!filedWords.contains(word)){
            filedWords.add(word)
        }
    }

    private fun wasChanged(word : StudyWord){
        if (!editWords.contains(word))
            editWords.add(word)
    }

    fun safe(){
        listDAO.update(studyList)
        if (editWords.isNotEmpty())
            wordDAO.updateAll(editWords)
    }

    private fun StudyWord.updateLastRepeat(){
        wasChanged(this)
        when(language){
            LanguageCase.Chin -> this.chnLastRepeat = GregorianCalendar.getInstance().time
            LanguageCase.Trn -> this.trnLastRepeat = GregorianCalendar.getInstance().time
            LanguageCase.Pin -> this.pinLastRepeat = GregorianCalendar.getInstance().time
        }
    }

    private fun StudyWord.lvl() : Int = when(language){
        LanguageCase.Chin -> this.chnLevel
        LanguageCase.Trn -> this.trnLevel
        LanguageCase.Pin -> this.pinLevel
    }

    private fun StudyWord.setLvl(lvl : Int) {
        wasChanged(this)
        when(language){
            LanguageCase.Chin -> this.chnLevel = lvl
            LanguageCase.Trn -> this.trnLevel = lvl
            LanguageCase.Pin -> this.pinLevel = lvl
        }
    }

    private fun StudyWord.repState() : Int = when(language){
        LanguageCase.Chin -> this.chnRepState
        LanguageCase.Trn -> this.trnRepState
        LanguageCase.Pin -> this.pinRepState
    }

    private fun StudyWord.setRepState(state: Int) {
        wasChanged(this)
        when(language){
            LanguageCase.Chin -> this.chnRepState = state
            LanguageCase.Trn -> this.trnRepState = state
            LanguageCase.Pin -> this.pinRepState = state
        }
    }

    private fun StudyWord.wordState() : StudyAnalyzerInterface.States = when(language){
            LanguageCase.Chin -> studyAnalyzer.getChnRepeatState(this)
            LanguageCase.Trn -> studyAnalyzer.getTrnRepeatState(this)
            LanguageCase.Pin -> studyAnalyzer.getPinRepeatState(this)

    }

    private fun StudyWord.lastRepeat() = when(language){
        LanguageCase.Chin -> this.chnLastRepeat
        LanguageCase.Trn -> this.trnLastRepeat
        LanguageCase.Pin -> this.pinLastRepeat

    }

    private fun StudyWord.wasTryRepeat() {
        wasChanged(this)
        when (language) {
            LanguageCase.Chin -> this.chnRepeat += 1
            LanguageCase.Trn -> this.trnRepeat += 1
            LanguageCase.Pin -> this.pinRepeat += 1
        }
    }


}
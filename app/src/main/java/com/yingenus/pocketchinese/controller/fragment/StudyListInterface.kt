package com.yingenus.pocketchinese.controller.fragment

import com.yingenus.pocketchinese.model.RepeatType
import com.yingenus.pocketchinese.model.database.pocketDB.StudyWord
import java.util.*

interface StudyListInterface {

    fun setNotify( canNotify : Boolean)
    fun setName(name : String)
    fun setWordsCount( items : Int)
    fun setChnStat( percent : Int)
    fun setPinStat(percent : Int)
    fun setTrnStat(percent : Int)
    fun lastRepeat( date: Date?)
    fun nextRepeat( date: Date?)
    fun setRepeatType(type : RepeatType)
    fun setStudyWords(stList: Map<Int, List<StudyWord>>)
    fun suggestMoveWord(words: List<StudyWord>, maxBlock : Int)
    fun suggestDeleteWords(words: List<StudyWord>)
    fun addWord(studyListUUID: UUID)
    fun editWord(studyWord: StudyWord, studyListUUID: UUID)
    fun startTrain(studyListUUID: UUID)


}
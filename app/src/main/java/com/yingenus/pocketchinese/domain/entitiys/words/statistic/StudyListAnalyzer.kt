package com.yingenus.pocketchinese.domain.entitiys.words.statistic

import com.yingenus.pocketchinese.domain.entitiys.RepeatType
import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.StudyWord
import java.util.*
import kotlin.math.floor

class StudyListAnalyzer(var repeatType: RepeatType = RepeatType.default, val repeatHelper: RepeatHelper ) {
    data class State(
            val words:Int,
            val lastRepeat:Date,
            val success:Int,
            val successChn: Int,
            val successPin: Int,
            val successTrn: Int,
            val worst : Int,
            val worstChn : Int,
            val worstPin : Int,
            val worstTrn : Int,
            val worstExpired: Int)


    private companion object{
        fun max(date1: Date,date2: Date): Date = if (date1.after(date2)) date1 else date2
        fun StudyWord.lastRepeat() : Date = max(chnLastRepeat, max(trnLastRepeat, pinLastRepeat))
    }

    fun getState( studyWords : List<StudyWord>): State{
        var lastRepeat : Date? = null
        var chnSuccess = 0
        var pinSuccess = 0
        var trnSuccess = 0
        var numb = 0
        var worstChn = 10
        var worstPin = 10
        var worstTrn = 10


        for (word in studyWords) {
            numb++
            chnSuccess += word.chnLevel
            pinSuccess += word.pinLevel
            trnSuccess += word.trnLevel

            if (worstChn > word.chnLevel)
                worstChn = word.chnLevel
            if (worstPin > word.pinLevel)
                worstPin = word.pinLevel
            if (worstTrn > word.trnLevel)
                worstTrn = word.trnLevel

            val lastRep = word.lastRepeat()

            if (lastRepeat == null )
                lastRepeat =lastRep
            else if (lastRepeat < lastRep)
                lastRepeat = lastRep
        }
        chnSuccess = floor(chnSuccess.div(numb.toDouble())).toInt()
        pinSuccess = floor(pinSuccess.div(numb.toDouble())).toInt()
        trnSuccess = floor(trnSuccess.div(numb.toDouble())).toInt()

        val success = getCommonSuccess(chnSuccess,pinSuccess,trnSuccess)
        val worst = getWorst(worstChn,worstPin,worstTrn)

        return State(numb, lastRepeat!!, success, chnSuccess, pinSuccess, trnSuccess, worst, worstChn, worstPin, worstTrn, howExpired(studyWords))
    }

    fun getPercentState (studyWords: List<StudyWord>): State{
        var lastRepeat : Date? = null
        var chnSuccess = 0
        var pinSuccess = 0
        var trnSuccess = 0
        var numb = 0
        var worstChn = 100
        var worstPin = 100
        var worstTrn = 100


        for (word in studyWords) {
            numb++
            chnSuccess += word.chnLevel * 10
            pinSuccess += word.pinLevel * 10
            trnSuccess += word.trnLevel * 10

            if (worstChn > word.chnLevel * 10)
                worstChn = word.chnLevel * 10
            if (worstPin > word.pinLevel * 10)
                worstPin = word.pinLevel * 10
            if (worstTrn > word.trnLevel * 10)
                worstTrn = word.trnLevel * 10

            val lastRep = word.lastRepeat()
            if (lastRepeat == null )
                lastRepeat = lastRep
            else if (lastRepeat < lastRep)
                lastRepeat = lastRep
        }
        chnSuccess = floor(chnSuccess.div(numb.toDouble())).toInt()
        pinSuccess = floor(pinSuccess.div(numb.toDouble())).toInt()
        trnSuccess = floor(trnSuccess.div(numb.toDouble())).toInt()

        val success = getCommonSuccess(chnSuccess,pinSuccess,trnSuccess)
        val worst = getWorst(worstChn,worstPin,worstTrn)

        return State(numb, lastRepeat!!, success, chnSuccess, pinSuccess, trnSuccess, worst, worstChn, worstPin, worstTrn, howExpired(studyWords))
    }

    private fun getCommonSuccess( successChn: Int, successPin: Int, successTrn: Int): Int{
        val ints = mutableListOf<Int>()

        if ( !repeatType.ignoreCHN)
            ints.add(successChn)
        if ( !repeatType.ignorePIN)
            ints.add(successPin)
        if ( !repeatType.ignoreTRN)
            ints.add(successTrn)

        return floor(ints.reduce { acc, i -> acc + i }/ints.size.toDouble()).toInt()
    }

    private fun getWorst( worstChn: Int, worstPin: Int, worstTrn: Int): Int{
        val ints = mutableListOf<Int>()

        if ( !repeatType.ignoreCHN)
            ints.add(worstChn)
        if ( !repeatType.ignorePIN)
            ints.add(worstPin)
        if ( !repeatType.ignoreTRN)
            ints.add(worstTrn)

        return ints.toTypedArray().min()!!
    }


    private fun howExpired(words : List<StudyWord>): Int =  words.map { howExpired(it) }.max() ?: RepeatHelper.Expired.GOOD

    private fun howExpired(word : StudyWord): Int{

        val pinEx = if (repeatType.ignorePIN) 0 else repeatHelper.howExpired(word.pinLastRepeat, word.pinLevel)
        val trnEx = if (repeatType.ignoreTRN) 0 else repeatHelper.howExpired(word.trnLastRepeat, word.trnLevel)
        val chEx = if (repeatType.ignoreCHN) 0 else repeatHelper.howExpired(word.chnLastRepeat, word.chnLevel)

        return Math.max(pinEx, Math.max(trnEx,chEx))
    }



}
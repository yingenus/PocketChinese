package com.yingenus.pocketchinese.domain.entitiys.words.statistic

import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.StudyWord

class StudyListRepeatStatistic{
    data class State(val bed:Int,val good:Int,val common:Int)

    private val studyAnalyzer: StudyAnalyzerInterface

    val chnState: State
    val pinState: State
    val trnState: State

    val chnBlockState:List<State>
    val pinBlockState:List<State>
    val trnBlockState:List<State>



    constructor(studyItems:Map<Int,List<StudyWord>>, studyAnalyzer: StudyAnalyzerInterface){
        this.studyAnalyzer=studyAnalyzer;

        chnBlockState=getChnStates(studyItems)
        chnState=mergeStates(chnBlockState)

        pinBlockState=getPinStates(studyItems)
        pinState=mergeStates(pinBlockState)

        trnBlockState=getTrnStates(studyItems)
        trnState=mergeStates(trnBlockState)
    }

    private fun mergeStates(list:List<State>)=list.reduce { acc, state ->
        State(acc.bed + state.bed, acc.good + state.good, acc.common + state.common)
    }

    private fun getChnStates(studyItems:Map<Int,List<StudyWord>>):List<State>{
        val ans:MutableList<State> = mutableListOf()
        for (key in studyItems.keys.sorted()){
            var good=0
            var bed=0
            var common=0
            for (studyWord in studyItems[key]!!){
                when(studyAnalyzer.getChnRepeatState(studyWord)){
                    StudyAnalyzerInterface.States.REPEATED->good++
                    StudyAnalyzerInterface.States.FAILED->bed++
                }
                common++
            }
            ans.add(State(bed, good, common))
        }
        return ans
    }

    private fun getPinStates(studyItems:Map<Int,List<StudyWord>>):List<State>{
        val ans:MutableList<State> = mutableListOf()
        for (key in studyItems.keys.sorted()){
            var good=0
            var bed=0
            var common=0
            for (studyWord in studyItems[key]!!){
                when(studyAnalyzer.getPinRepeatState(studyWord)){
                    StudyAnalyzerInterface.States.REPEATED->good++
                    StudyAnalyzerInterface.States.FAILED->bed++
                }
                common++
            }
            ans.add(State(bed, good, common))
        }
        return ans
    }

    private fun getTrnStates(studyItems:Map<Int,List<StudyWord>>):List<State>{
        val ans:MutableList<State> = mutableListOf()
        for (key in studyItems.keys.sorted()){
            var good=0
            var bed=0
            var common=0
            for (studyWord in studyItems[key]!!){
                when(studyAnalyzer.getTrnRepeatState(studyWord)){
                    StudyAnalyzerInterface.States.REPEATED->good++
                    StudyAnalyzerInterface.States.FAILED->bed++
                }
                common++
            }
            ans.add(State(bed, good, common))
        }
        return ans
    }

}
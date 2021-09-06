package com.yingenus.pocketchinese.model.words.statistic

import com.yingenus.pocketchinese.model.LanguageCase
import com.yingenus.pocketchinese.model.database.pocketDB.StudyWord

class StudyListFilter(val analyzer: StudyAnalyzerInterface){
    fun filter(studyWords:List<StudyWord>, language: LanguageCase, states:List<StudyAnalyzerInterface.States>):List<StudyWord>{
        return when(language){
            LanguageCase.Chin->filterChn(studyWords,states)
            LanguageCase.Trn->filterTrn(studyWords, states)
            LanguageCase.Pin->filterPin(studyWords, states)
        }
    }
    private fun filterPin(studyWords:List<StudyWord>, states:List<StudyAnalyzerInterface.States>) = studyWords.filter { states.contains(analyzer.getPinRepeatState(it))}
    private fun filterChn(studyWords:List<StudyWord>, states:List<StudyAnalyzerInterface.States>) = studyWords.filter { states.contains(analyzer.getChnRepeatState(it))}
    private fun filterTrn(studyWords:List<StudyWord>, states:List<StudyAnalyzerInterface.States>) = studyWords.filter { states.contains(analyzer.getTrnRepeatState(it))}

}
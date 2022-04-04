package com.yingenus.pocketchinese.presentation.views.creteeditword

import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.StudyWord
import io.reactivex.rxjava3.core.Observable


interface CreateWordInterface {

    enum class FIELD{
        TRN, PIN, CHN
    }

    fun getChnInputObserver(): Observable<String>

    fun getSelectStudyListObservable(): Observable<String>

    fun wordCreated()

    fun showEmtFieldMes(field : FIELD, show : Boolean)

    fun showInvalCharsMes(field : FIELD, show : Boolean)

    fun showMaxCharsMes(field : FIELD, show : Boolean)

    fun getMaxCharsLength(field : FIELD): Int

    fun getSelectedBlock(): Int

    fun setBlocks(items : Int)

    fun getText(field : FIELD):String

    fun setText(field : FIELD, text : String)

    fun setHint(field : FIELD, hint : String)

    fun setListOfStudyLists(list : List<String>)

    fun setSelectStudyList(position : Int)

    fun setSelectBlock(position : Int)

    fun enableChoseStudyList(enable : Boolean)

    fun analogFounded(studyWord: StudyWord)
}
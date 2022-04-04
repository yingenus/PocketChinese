package com.yingenus.pocketchinese.presentation.views.userlist

import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.StudyList
import java.util.*

interface UserListsInterface {

    data class StudyListInfo(val studyList : StudyList, var lastRepeat: Date, var success : Int, var expered : Expired, var wordsCount : Int)
    enum class Expired{
        NON,GOOD,MEDIUM,BED
    }

    fun showStartView();
    fun setStudyLists( lists : List<StudyListInfo>)

}
package com.yingenus.pocketchinese.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yingenus.pocketchinese.domain.dto.StudyList
import java.util.*
import kotlin.math.tanh

@Entity(tableName = "study_list")
class StudyList(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo( name = "id") val id : Long,
    @ColumnInfo(name = "name") val name : String,
    @ColumnInfo(name = "create_date") val createDate: Date,
    @ColumnInfo(name = "notify") val notify : Boolean
) {

    companion object{
        fun fromStudyList(studyList: StudyList) : com.yingenus.pocketchinese.data.local.room.entity.StudyList =
            com.yingenus.pocketchinese.data.local.room.entity.StudyList(
                id = studyList.id,
                name = studyList.name,
                createDate = Date(System.currentTimeMillis()),
                notify = studyList.notifyUser
            )
    }

    fun toStudyList(): StudyList = StudyList(
        id = id,
        name = name,
        notifyUser = notify,
        createDate = createDate
    )
}

class StudyListUpdate(
    val id : Long,
    val name : String,
    val notify : Boolean
){

    companion object{
        fun fromStudyList(studyList: StudyList) : StudyListUpdate =
            StudyListUpdate(
                id = studyList.id,
                name = studyList.name,
                notify = studyList.notifyUser
            )
    }
}
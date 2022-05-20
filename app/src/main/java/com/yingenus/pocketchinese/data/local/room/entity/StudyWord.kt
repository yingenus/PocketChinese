package com.yingenus.pocketchinese.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yingenus.pocketchinese.domain.dto.StudyWord
import java.util.*

@Entity(tableName = "study_words")
class StudyWord (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo( name = "wordchn") val chinese : String,
    @ColumnInfo( name = "pinyin") val pinyin : String,
    @ColumnInfo( name = "translate") val translation : String,
    @ColumnInfo( name = "study_list") val studyListId: Long,
    @ColumnInfo( name = "create_date") val createDate: Date
        )
{

    companion object{
        fun fromStudyWord( studyListId: Long, studyWord: StudyWord) : com.yingenus.pocketchinese.data.local.room.entity.StudyWord =
            com.yingenus.pocketchinese.data.local.room.entity.StudyWord(
                id = studyWord.id,
                chinese = studyWord.chinese,
                pinyin = studyWord.pinyin,
                translation = studyWord.translate,
                studyListId = studyListId,
                createDate = studyWord.createDate
            )
    }

    fun toStudyWord(): StudyWord =
        StudyWord(
            id = id,
            chinese = chinese,
            pinyin = pinyin,
            translate = translation,
            createDate = createDate
        )
}

class StudyWordUpdate(
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo( name = "wordchn") val chinese : String,
    @ColumnInfo( name = "pinyin") val pinyin : String,
    @ColumnInfo( name = "translate") val translation : String,
    @ColumnInfo( name = "create_date") val createDate: Date
){
    companion object{
        fun fromStudyList( studyWord: StudyWord) :  StudyWordUpdate =
            StudyWordUpdate(
                studyWord.id,
                studyWord.chinese,
                studyWord.pinyin,
                studyWord.translate,
                studyWord.createDate
            )
    }
}
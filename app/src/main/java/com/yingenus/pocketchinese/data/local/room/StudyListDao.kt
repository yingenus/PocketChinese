package com.yingenus.pocketchinese.data.local.room

import androidx.room.*
import com.yingenus.pocketchinese.data.local.room.entity.StudyList
import com.yingenus.pocketchinese.data.local.room.entity.StudyListUpdate
import com.yingenus.pocketchinese.data.local.room.entity.StudyWord

@Dao
interface StudyListDao {
    @Query("SELECT * FROM study_list")
    fun getAll(): List<StudyList>

    @Query("SELECT * FROM study_list WHERE name = :name")
    fun getByName(name: String): StudyList?

    @Query("SELECT * FROM study_list WHERE id = :id")
    fun getById(id : Long): StudyList?

    @Query("SELECT study_list.* FROM study_list INNER JOIN study_words ON study_list.id == study_words.study_list WHERE study_words.id == :id")
    fun getByWord(id : Long): StudyList?

    @Insert
    fun creteStudyList(studyList: StudyList)

    @Update
    fun updateStudyList(studyList: StudyList)

    @Update(entity = StudyList::class)
    fun updateStudyList(studyList: StudyListUpdate)

    @Delete
    fun deleteStudyList(studyList: StudyList)

    @Delete
    fun deleteStudyListAndWords(studyList: StudyList, studyWords: List<StudyWord>)

}
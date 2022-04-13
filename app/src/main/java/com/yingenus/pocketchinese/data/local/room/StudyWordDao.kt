package com.yingenus.pocketchinese.data.local.room

import androidx.room.*
import com.yingenus.pocketchinese.data.local.room.entity.StudyList
import com.yingenus.pocketchinese.data.local.room.entity.StudyWord
import com.yingenus.pocketchinese.data.local.room.entity.StudyWordUpdate

@Dao
interface StudyWordDao {

    @Query("SELECT * FROM words")
    fun getAll(): List<StudyWord>

    @Query("SELECT * FROM words WHERE studylist = :studyListId")
    fun getStudyWords(studyListId: Long): List<StudyWord>

    @Query("SELECT COUNT(*) FROM words WHERE studylist = :studyListId")
    fun wordsInStudyList(studyListId : Long): Int

    @Query("SELECT * FROM words WHERE id = :id")
    fun getStudyWord(id: Long): StudyWord?

    @Update
    fun updateStudyWord(studyWord: StudyWord)

    @Update
    fun updateStudyWords( words : List<StudyWord>)

    @Update(entity = StudyWord::class)
    fun updateStudyWord(studyWord: StudyWordUpdate)

    @Update(entity = StudyWord::class)
    fun updateStudyWordsU( words : List<StudyWordUpdate>)

    @Insert
    fun creteStudyWord(studyWord: StudyWord)
    @Insert
    fun creteStudyWords( words : List<StudyWord>)

    @Delete
    fun deleteStudyWord(studyWord: StudyWord)

    @Delete
    fun deleteStudyWords( words : List<StudyWord>)

    @Delete(entity = StudyWord::class)
    fun deleteStudyWord(studyWord: StudyWordUpdate)

    @Delete(entity = StudyWord::class)
    fun deleteStudyWordsU( words : List<StudyWordUpdate>)

    @Query( "DELETE FROM words WHERE id IN (:words)")
    fun deleteStudyWordsById( words : List<Long>)

}
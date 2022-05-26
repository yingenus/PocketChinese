package com.yingenus.pocketchinese.data.local.room

import androidx.room.*
import com.yingenus.pocketchinese.data.local.room.entity.Repeat
import com.yingenus.pocketchinese.data.local.room.entity.StudyWord
import java.util.*

@Dao
interface RepeatDao {

    @Query( "SELECT * FROM repeat WHERE word_id = :studyWordId" )
    fun getRepeat( studyWordId: Long): Repeat?

    @Query("SELECT repeat.* FROM repeat INNER JOIN study_words ON study_words.id = repeat.word_id WHERE study_words.study_list = :studyListId" )
    fun getRepeatsForList( studyListId: Long): List<Repeat>

    @Update
    fun updateRepeat( repeat: Repeat)
    @Insert
    fun creteRepeat ( repeat: Repeat)

    @Delete
    fun deleteRepeat ( repeat: Repeat)
    @Delete
    fun deleteRepeats ( repeat: List<Repeat>)

    @Query( "DELETE FROM repeat WHERE word_id IN ( SELECT study_words.id FROM study_words WHERE study_words.study_list = :studyListId)")
    fun deleteRepeatByStudyListId( studyListId: Long)

    @Query( "DELETE FROM repeat WHERE word_id = :studyWordId")
    fun deleteByWordId( studyWordId: Long)

    @Query( "DELETE FROM repeat WHERE word_id IN (:studyWordIds)")
    fun deleteByWordIds( studyWordIds: List<Long>)

}
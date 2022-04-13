package com.yingenus.pocketchinese.data.local.room

import androidx.room.*
import com.yingenus.pocketchinese.data.local.room.entity.Repeat
import com.yingenus.pocketchinese.data.local.room.entity.StudyWord
import java.util.*

@Dao
interface RepeatDao {

    @Query( "SELECT * FROM repeat WHERE word_id = :studyWordId" )
    fun getRepeat( studyWordId: Long): Repeat?

    @Query("SELECT repeat.* FROM repeat INNER JOIN study_list ON words.id = repeat.word_id WHERE words.studylist = :studyListId" )
    fun getRepeatsForList( studyListId: Long): List<Repeat>

    @Update
    fun updateRepeat( repeat: Repeat)
    @Insert
    fun creteRepeat ( repeat: Repeat)
    @Delete
    fun deleteRepeat ( repeat: Repeat)
    @Delete
    fun deleteRepeats ( repeat: List<Repeat>)

    @Query( "DELETE FROM repeat WHERE word_id = :studyWordId")
    fun deleteByWordId( studyWordId: Long)

    @Query( "DELETE FROM repeat WHERE word_id IN (:studyWordIds)")
    fun deleteByWordIds( studyWordIds: List<Long>)

}
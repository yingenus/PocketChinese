package com.yingenus.pocketchinese.data.local.room
import androidx.room.Dao
import androidx.room.Query
import com.yingenus.pocketchinese.data.local.room.entity.ExampleLink

@Dao
interface ExampleLinkDao {
    @Query("SELECT * FROM links")
    fun loadAll(): List<ExampleLink>
    @Query("SELECT * FROM links WHERE word_id = :wordId")
    fun loadByWordId( wordId : Int): ExampleLink?
}
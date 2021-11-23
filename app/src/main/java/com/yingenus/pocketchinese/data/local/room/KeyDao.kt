package com.yingenus.pocketchinese.data.local.room

import androidx.room.Dao
import androidx.room.Query
import com.yingenus.pocketchinese.data.local.room.entity.Key

@Dao
interface KeyDao {
    @Query("SELECT * FROM zi_key")
    fun getAll(): List<Key>
    @Query("SELECT * FROM zi_key WHERE radical = :radical")
    fun loadByRadical( radical : String): List<Key>
}
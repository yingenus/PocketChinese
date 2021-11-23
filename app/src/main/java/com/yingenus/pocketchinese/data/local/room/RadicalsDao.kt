package com.yingenus.pocketchinese.data.local.room

import androidx.room.Dao
import androidx.room.Query
import com.yingenus.pocketchinese.data.local.room.entity.Radical

@Dao
interface RadicalsDao {
    @Query("SELECT * FROM zi_radical")
    fun getAll(): List<Radical>
}
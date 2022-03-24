package com.yingenus.pocketchinese.data.local.sqlite.dao

import androidx.room.Dao
import androidx.room.Query
import com.yingenus.pocketchinese.data.local.sqlite.entity.Radical

@Dao
interface RadicalsDao {
    @Query("SELECT * FROM zi_radical")
    fun getAll(): List<Radical>
}
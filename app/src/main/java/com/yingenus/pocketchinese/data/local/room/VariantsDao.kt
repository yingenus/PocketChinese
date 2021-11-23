package com.yingenus.pocketchinese.data.local.room

import androidx.room.Dao
import androidx.room.Query
import com.yingenus.pocketchinese.data.local.room.entity.PinVariants

@Dao
interface VariantsDao {
    @Query("SELECT * FROM py_variants")
    fun getAll(): List<PinVariants>
}
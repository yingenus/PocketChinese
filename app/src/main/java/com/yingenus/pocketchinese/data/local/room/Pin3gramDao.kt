package com.yingenus.pocketchinese.data.local.room

import androidx.room.Dao
import androidx.room.Query
import com.yingenus.pocketchinese.data.local.room.entity.Pin3gram
import com.yingenus.pocketchinese.data.local.room.entity.Rus3gram

@Dao
interface Pin3gramDao {
    @Query("SELECT * FROM pin_3grams WHERE ngram = :ngram AND position = :position")
    fun getNgram(ngram : String, position : Int): Pin3gram?
    @Query("SELECT * FROM pin_3grams WHERE ngram = :ngram AND position IN (:position)")
    fun getNgrams(ngram : String, position : IntArray): List<Pin3gram>
    @Query("SELECT * FROM pin_3grams")
    fun getAll(): List<Pin3gram>
}
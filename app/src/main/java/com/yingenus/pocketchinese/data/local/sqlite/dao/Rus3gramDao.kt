package com.yingenus.pocketchinese.data.local.sqlite.dao

import androidx.room.Dao
import androidx.room.Query
import com.yingenus.pocketchinese.data.local.sqlite.entity.Pin3gram
import com.yingenus.pocketchinese.data.local.sqlite.entity.Rus3gram

@Dao
interface Rus3gramDao {
    @Query("SELECT * FROM rus_3grams WHERE ngram = :ngram AND position = :position")
    fun getNgram(ngram : String, position : Int): Rus3gram?
    @Query("SELECT * FROM rus_3grams WHERE ngram = :ngram AND position IN (:position)")
    fun getNgrams(ngram : String, position : IntArray): List<Rus3gram>
    @Query("SELECT * FROM rus_3grams")
    fun getAll(): List<Rus3gram>
}
package com.yingenus.pocketchinese.data.local.sqlite.dao

import androidx.room.Dao
import androidx.room.Query
import com.yingenus.pocketchinese.data.local.sqlite.entity.Chn1Gram
import com.yingenus.pocketchinese.data.local.sqlite.entity.Chn2Gram

@Dao
interface ChnNgramDao {
    @Query("SELECT * FROM chin_1gram WHERE ngram = :ngram")
    fun get1gram(ngram : String): Chn1Gram?
    @Query("SELECT * FROM chin_2gram WHERE ngram = :ngram")
    fun get2gram(ngram : String): Chn2Gram?
}
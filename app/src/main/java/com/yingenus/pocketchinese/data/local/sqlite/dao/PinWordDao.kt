package com.yingenus.pocketchinese.data.local.sqlite.dao

import androidx.room.Dao
import androidx.room.Query
import com.yingenus.pocketchinese.data.local.sqlite.entity.PinWord
import com.yingenus.pocketchinese.data.local.sqlite.entity.RusWord

@Dao
interface PinWordDao {
    @Query("SELECT * FROM pin_words")
    fun getAll(): List<PinWord>
    @Query("SELECT * FROM pin_words WHERE word_id = :wordId")
    fun getWord(wordId : Int): PinWord?
    @Query("SELECT * FROM pin_words WHERE word_id IN (:wordsIds)")
    fun getWords(wordsIds : IntArray) : List<PinWord>
    @Query("SELECT MAX(word_id) FROM pin_words")
    fun getMaxId() : Int
}
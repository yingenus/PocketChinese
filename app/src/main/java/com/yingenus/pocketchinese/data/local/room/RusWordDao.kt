package com.yingenus.pocketchinese.data.local.room

import androidx.room.Dao
import androidx.room.Query
import com.yingenus.pocketchinese.data.local.room.entity.PinWord
import com.yingenus.pocketchinese.data.local.room.entity.RusWord

@Dao
interface RusWordDao {
    @Query("SELECT * FROM rus_words")
    fun getAll(): List<RusWord>
    @Query("SELECT * FROM rus_words WHERE word_id = :wordId")
    fun getWord(wordId : Int): RusWord?
    @Query("SELECT * FROM rus_words WHERE word_id IN (:wordsIds)")
    fun getWords(wordsIds : IntArray) : List<RusWord>
    @Query("SELECT MAX(word_id) FROM rus_words")
    fun getMaxId() : Int

}
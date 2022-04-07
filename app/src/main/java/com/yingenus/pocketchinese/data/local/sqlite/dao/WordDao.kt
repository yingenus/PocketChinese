package com.yingenus.pocketchinese.data.local.sqlite.dao

import androidx.room.Dao
import androidx.room.Query
import com.yingenus.pocketchinese.data.local.sqlite.entity.Word

@Dao
interface WordDao {
    @Query("SELECT * FROM words")
    fun getAll(): List<Word>
    @Query("SELECT * FROM words WHERE ID = :id")
    fun loadById( id : Int): Word?
    @Query("SELECT * FROM words WHERE chinese_word LIKE '%' || :entry || '%'")
    fun loadByEntryChinese( entry : String): List<Word>
    @Query("SELECT * FROM words WHERE pinyin LIKE '%' || :entry || '%'")
    fun loadByEntryPinyin( entry : String): List<Word>
    @Query("SELECT * FROM words WHERE translation LIKE '%' || :entry || '%'")
    fun loadByEntryTranslation( entry : String): List<Word>
}
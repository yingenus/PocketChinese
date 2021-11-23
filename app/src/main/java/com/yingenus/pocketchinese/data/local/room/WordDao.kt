package com.yingenus.pocketchinese.data.local.room

import androidx.room.Dao
import androidx.room.Query
import com.yingenus.pocketchinese.data.local.room.entity.Word

@Dao
interface WordDao {
    @Query("SELECT * FROM words")
    fun getAll(): List<Word>
    @Query("SELECT * FROM words WHERE ID = :id")
    fun loadById( id : Int): Word?
    @Query("SELECT * FROM words WHERE chinese_word = '%' || :entry || '%'")
    fun loadByEntryChinese( entry : String): List<Word>
    @Query("SELECT * FROM words WHERE pinyin = '%' || :entry || '%'")
    fun loadByEntryPinyin( entry : String): List<Word>
    @Query("SELECT * FROM words WHERE translation LIKE '%' || :entry || '%'")
    fun loadByEntryTranslation( entry : String): List<Word>
}
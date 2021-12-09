package com.yingenus.pocketchinese.data.local.room

import androidx.room.Dao
import androidx.room.Query
import com.yingenus.pocketchinese.data.local.room.entity.Example

@Dao
interface ExampleDao {
    @Query("SELECT * FROM examples")
    fun getAll(): List<Example>
    @Query("SELECT * FROM examples WHERE id = :id")
    fun loadById( id : Int): Example?
    //max size of result is 50
    @Query(
        "SELECT examples.id, examples.chinese_word, examples.pinyin,examples.translation, examples.entry_words  FROM examples, links WHERE links.word_id = :id  AND ( links.exmpl_ids = examples.id OR links.exmpl_ids GLOB '*[^0-9]' || examples.id || '[^0-9]*' OR links.exmpl_ids GLOB examples.id || '[^0-9]*' OR links.exmpl_ids GLOB '*[^0-9]' || examples.id) LIMIT 50")
    fun loadByWordId( id : Int): List<Example>
    @Query(
        "SELECT examples.id, examples.chinese_word, examples.pinyin,examples.translation, examples.entry_words  FROM examples, links WHERE links.word_id = :id  AND ( links.exmpl_ids = examples.id OR links.exmpl_ids GLOB '*[^0-9]' || examples.id || '[^0-9]*' OR links.exmpl_ids GLOB examples.id || '[^0-9]*' OR links.exmpl_ids GLOB '*[^0-9]' || examples.id) LIMIT :limit")
    fun loadByWordIdLimited( id : Int, limit : Int): List<Example>


}
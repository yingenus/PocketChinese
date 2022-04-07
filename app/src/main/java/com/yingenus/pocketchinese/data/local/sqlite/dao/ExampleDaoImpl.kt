package com.yingenus.pocketchinese.data.local.sqlite.dao

import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.dao.GenericRawResults
import com.j256.ormlite.stmt.PreparedQuery
import com.j256.ormlite.support.ConnectionSource
import com.yingenus.pocketchinese.data.local.sqlite.entity.Example


class ExampleDaoImpl(connectionSource: ConnectionSource) : BaseDaoImpl<Example, String>(connectionSource, Example::class.java),
    ExampleDao {
    //@Query("SELECT * FROM examples")
    override fun getAll(): List<Example>{
        return queryForAll()
    }
    //@Query("SELECT * FROM examples WHERE id = :id")
    override fun loadById( id : Int): Example?{
        val preparedQuery : PreparedQuery<Example> =
            queryBuilder().where().eq("id", id).prepare()

        val result = query(preparedQuery)
        return result.firstOrNull()
    }

    //max size of result is 50
    //@Query(
    //    "SELECT examples.id, examples.chinese_word, examples.pinyin,examples.translation, examples.entry_words  FROM examples, links WHERE links.word_id = :id  AND ( links.exmpl_ids = examples.id OR links.exmpl_ids GLOB '*[^0-9]' || examples.id || '[^0-9]*' OR links.exmpl_ids GLOB examples.id || '[^0-9]*' OR links.exmpl_ids GLOB '*[^0-9]' || examples.id) LIMIT 50")
    override fun loadByWordId(id: Int): List<Example> {
        val genericRawResults: GenericRawResults<Example> = queryRaw(
            "SELECT examples.id, examples.chinese_word, examples.pinyin,examples.translation, examples.entry_words  FROM examples, links WHERE links.word_id = $id  AND ( links.exmpl_ids = examples.id OR links.exmpl_ids GLOB '*[^0-9]' || examples.id || '[^0-9]*' OR links.exmpl_ids GLOB examples.id || '[^0-9]*' OR links.exmpl_ids GLOB '*[^0-9]' || examples.id) LIMIT 50",
            rawRowMapper
        )

        return genericRawResults.results
    }
    //@Query(
    //    "SELECT examples.id, examples.chinese_word, examples.pinyin,examples.translation, examples.entry_words  FROM examples, links WHERE links.word_id = :id  AND ( links.exmpl_ids = examples.id OR links.exmpl_ids GLOB '*[^0-9]' || examples.id || '[^0-9]*' OR links.exmpl_ids GLOB examples.id || '[^0-9]*' OR links.exmpl_ids GLOB '*[^0-9]' || examples.id) LIMIT :limit")
    override fun loadByWordIdLimited( id : Int, limit : Int): List<Example>{
        val genericRawResults: GenericRawResults<Example> = queryRaw(
            "SELECT examples.id, examples.chinese_word, examples.pinyin,examples.translation, examples.entry_words  FROM examples, links WHERE links.word_id = $id  AND ( links.exmpl_ids = examples.id OR links.exmpl_ids GLOB '*[^0-9]' || examples.id || '[^0-9]*' OR links.exmpl_ids GLOB examples.id || '[^0-9]*' OR links.exmpl_ids GLOB '*[^0-9]' || examples.id) LIMIT $limit",
            rawRowMapper
        )

        return genericRawResults.results
    }


}
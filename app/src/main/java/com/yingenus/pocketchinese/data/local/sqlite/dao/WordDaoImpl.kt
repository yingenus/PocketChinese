package com.yingenus.pocketchinese.data.local.sqlite.dao

import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.stmt.PreparedQuery
import com.j256.ormlite.support.ConnectionSource
import com.yingenus.pocketchinese.data.local.sqlite.entity.Word


class WordDaoImpl(connectionSource: ConnectionSource) : BaseDaoImpl<Word,String>(connectionSource, Word::class.java),
    WordDao {
    //@Query("SELECT * FROM words")
    override fun getAll(): List<Word>{
        return queryForAll()
    }
    //@Query("SELECT * FROM words WHERE ID = :id")
    override fun loadById( id : Int): Word?{
        val preparedQuery : PreparedQuery<Word> =
            queryBuilder().where().eq("ID",id)
                .prepare()
        val result = query(preparedQuery)
        return result.firstOrNull()
    }
    //@Query("SELECT * FROM words WHERE chinese_word LIKE '%' || :entry || '%'")
    override fun loadByEntryChinese( entry : String): List<Word>{
        val preparedQuery : PreparedQuery<Word> =
            queryBuilder().where().like("chinese_word", "'%${entry}%'").prepare()
        return query(preparedQuery)
    }
    //@Query("SELECT * FROM words WHERE pinyin LIKE '%' || :entry || '%'")
    override fun loadByEntryPinyin( entry : String): List<Word>{
        val preparedQuery : PreparedQuery<Word> =
            queryBuilder().where().like("pinyin", "'%${entry}%'").prepare()
        return query(preparedQuery)
    }
    //@Query("SELECT * FROM words WHERE translation LIKE '%' || :entry || '%'")
    override fun loadByEntryTranslation( entry : String): List<Word>{
        val preparedQuery : PreparedQuery<Word> =
            queryBuilder().where().like("translation", "'%${entry}%'").prepare()
        return query(preparedQuery)
    }
}
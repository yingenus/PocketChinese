package com.yingenus.pocketchinese.data.local.sqlite.dao

import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.dao.GenericRawResults
import com.j256.ormlite.stmt.PreparedQuery
import com.j256.ormlite.support.ConnectionSource
import com.yingenus.pocketchinese.data.local.sqlite.entity.PinWord


class PinWordDaoImpl(connectionSource: ConnectionSource) : BaseDaoImpl<PinWord,String>(connectionSource, PinWord::class.java),
    PinWordDao {
    //@Query("SELECT * FROM pin_words")
    override fun getAll(): List<PinWord>{
        return queryForAll()
    }
    //@Query("SELECT * FROM pin_words WHERE word_id = :wordId")
    override fun getWord(wordId : Int): PinWord?{
        val preparedQuery: PreparedQuery<PinWord> =
            queryBuilder().where().eq("word_id",wordId).prepare()

        val result = query(preparedQuery)
        return result.firstOrNull()
    }
    //@Query("SELECT * FROM pin_words WHERE word_id IN (:wordsIds)")
    override fun getWords(wordsIds : IntArray) : List<PinWord>{
        val preparedQuery: PreparedQuery<PinWord> =
            queryBuilder().where().`in`("word_id",wordsIds).prepare()

        return query(preparedQuery)
    }
    //@Query("SELECT MAX(word_id) FROM pin_words")
    override fun getMaxId() : Int{
        val genericRawResults : GenericRawResults<Array<String>> = queryRaw("SELECT MAX(word_id) FROM pin_words")
        return genericRawResults.results.firstOrNull()?.firstOrNull()?.toIntOrNull()?:0
    }
}
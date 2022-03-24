package com.yingenus.pocketchinese.data.local.sqlite.dao

import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.dao.GenericRawResults
import com.j256.ormlite.stmt.PreparedQuery
import com.j256.ormlite.support.ConnectionSource
import com.yingenus.pocketchinese.data.local.sqlite.entity.RusWord


class RusWordDaoImpl(connectionSource: ConnectionSource) : BaseDaoImpl<RusWord, String>(connectionSource, RusWord::class.java),
    RusWordDao {
    //@Query("SELECT * FROM rus_words")
    override fun getAll(): List<RusWord>{
        return queryForAll()
    }
    //@Query("SELECT * FROM rus_words WHERE word_id = :wordId")
    override fun getWord(wordId : Int): RusWord?{
        val preparedQuery : PreparedQuery<RusWord> =
            queryBuilder().where().eq("word_id",wordId)
                .prepare()
        val result = query(preparedQuery)
        return result.firstOrNull()
    }
    //@Query("SELECT * FROM rus_words WHERE word_id IN (:wordsIds)")
    override fun getWords(wordsIds : IntArray) : List<RusWord>{
        val preparedQuery : PreparedQuery<RusWord> =
            queryBuilder().where().eq("word_id",wordsIds).prepare()
        return query(preparedQuery)
    }
    //@Query("SELECT MAX(word_id) FROM rus_words")
    override fun getMaxId() : Int{
        val genericRawResults : GenericRawResults<Array<String>> = queryRaw("SELECT MAX(word_id) FROM rus_words")
        return genericRawResults.results.firstOrNull()?.firstOrNull()?.toIntOrNull()?:0
    }

}
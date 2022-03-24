package com.yingenus.pocketchinese.data.local.sqlite.dao

import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.stmt.PreparedQuery
import com.j256.ormlite.support.ConnectionSource
import com.yingenus.pocketchinese.data.local.sqlite.entity.Rus3gram


class Rus3gramDaoImpl(connectionSource: ConnectionSource) : BaseDaoImpl<Rus3gram, String>(connectionSource, Rus3gram::class.java),
    Rus3gramDao {
    //@Query("SELECT * FROM rus_3grams WHERE ngram = :ngram AND position = :position")
    override fun getNgram(ngram : String, position : Int): Rus3gram?{
        val preparedQuery : PreparedQuery<Rus3gram> = queryBuilder().where()
            .eq("ngram",ngram)
            .and().eq("position",position)
            .prepare()

        val result = query(preparedQuery)
        return result.firstOrNull()
    }
    //@Query("SELECT * FROM rus_3grams WHERE ngram = :ngram AND position IN (:position)")
    override fun getNgrams(ngram : String, position : IntArray): List<Rus3gram>{
        val preparedQuery : PreparedQuery<Rus3gram> = queryBuilder().where()
            .eq("ngram",ngram).and()
            .`in`("position",position)
            .prepare()
        return query(preparedQuery)
    }
    //@Query("SELECT * FROM rus_3grams")
    override fun getAll(): List<Rus3gram>{
        return queryForAll()
    }
}
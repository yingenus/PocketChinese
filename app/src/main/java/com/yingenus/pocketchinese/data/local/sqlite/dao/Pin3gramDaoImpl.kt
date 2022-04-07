package com.yingenus.pocketchinese.data.local.sqlite.dao

import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.stmt.PreparedQuery
import com.j256.ormlite.support.ConnectionSource
import com.yingenus.pocketchinese.data.local.sqlite.entity.Pin3gram


class Pin3gramDaoImpl(connectionSource: ConnectionSource) : BaseDaoImpl<Pin3gram, String>(connectionSource,Pin3gram::class.java),
    Pin3gramDao {
    //@Query("SELECT * FROM pin_3grams WHERE ngram = :ngram AND position = :position")
    override fun getNgram(ngram : String, position : Int): Pin3gram?{
        val preparedQuery : PreparedQuery<Pin3gram> =
            queryBuilder().where().eq("ngram",ngram)
                .and().eq("position",position)
                .prepare()

        val result = query(preparedQuery)
        return result.firstOrNull()
    }
    //@Query("SELECT * FROM pin_3grams WHERE ngram = :ngram AND position IN (:position)")
    override fun getNgrams(ngram : String, position : IntArray): List<Pin3gram>{
        val preparedQuery : PreparedQuery<Pin3gram> =
            queryBuilder().where().eq("ngram",ngram)
                .and().`in`("position",position)
                .prepare()

        return query(preparedQuery)
    }
    //@Query("SELECT * FROM pin_3grams")
    override fun getAll(): List<Pin3gram>{
        return queryForAll()
    }
}
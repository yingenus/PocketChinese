package com.yingenus.pocketchinese.data.local.sqlite.dao

import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.stmt.PreparedQuery
import com.j256.ormlite.support.ConnectionSource
import com.yingenus.pocketchinese.data.local.sqlite.entity.Chn1Gram
import com.yingenus.pocketchinese.data.local.sqlite.entity.Chn2Gram

interface Chn1GramDao{
    fun get1Gram(ngram : String): Chn1Gram?
}
interface Chn2GramDao{
    fun get2Gram(ngram : String): Chn2Gram?
}

class Chn1GramDaoImpl(connection: ConnectionSource): BaseDaoImpl<Chn1Gram, String>(connection, Chn1Gram::class.java), Chn1GramDao{
    override fun get1Gram(ngram : String): Chn1Gram?{
        val preparedQuery : PreparedQuery<Chn1Gram> = queryBuilder()
            .where().eq("ngram", ngram)
            .prepare()

        val results = query(preparedQuery)

        return results.firstOrNull()
    }
}
class Chn2GramDaoImpl(connection: ConnectionSource): BaseDaoImpl<Chn2Gram, String>(connection, Chn2Gram::class.java), Chn2GramDao{
    override fun get2Gram(ngram : String): Chn2Gram?{
        val preparedQuery : PreparedQuery<Chn2Gram> = queryBuilder()
            .where().eq("ngram", ngram)
            .prepare()

        val results = query(preparedQuery)

        return results.firstOrNull()
    }
}
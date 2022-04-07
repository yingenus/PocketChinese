package com.yingenus.pocketchinese.data.local.sqlite.dao

import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.stmt.PreparedQuery
import com.j256.ormlite.support.ConnectionSource
import com.yingenus.pocketchinese.data.local.sqlite.entity.Chn1Gram
import com.yingenus.pocketchinese.data.local.sqlite.entity.Chn2Gram

class ChnNgramDaoImpl(connection : ConnectionSource) : ChnNgramDao {

    private val gram1Dao : Chn1GramDao
    private val gram2Dao : Chn2GramDao

    init {
        gram1Dao = Chn1GramDaoImpl(connection)
        gram2Dao = Chn2GramDaoImpl(connection)
    }

    //@Query("SELECT * FROM chin_1gram WHERE ngram = :ngram")
    override fun get1gram(ngram : String): Chn1Gram? =
        gram1Dao.get1Gram(ngram)
    //@Query("SELECT * FROM chin_2gram WHERE ngram = :ngram")
    override fun get2gram(ngram : String): Chn2Gram? =
        gram2Dao.get2Gram(ngram)
}



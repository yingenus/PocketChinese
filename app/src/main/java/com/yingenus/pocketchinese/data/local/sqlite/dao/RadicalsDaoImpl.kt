package com.yingenus.pocketchinese.data.local.sqlite.dao

import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.support.ConnectionSource
import com.yingenus.pocketchinese.data.local.sqlite.entity.Radical

class RadicalsDaoImpl(connectionSource: ConnectionSource) : BaseDaoImpl<Radical, String>(connectionSource, Radical::class.java),
    RadicalsDao {
    //@Query("SELECT * FROM zi_radical")
    override fun getAll(): List<Radical>{
        return queryForAll()
    }
}
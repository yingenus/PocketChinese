package com.yingenus.pocketchinese.data.local.sqlite.dao

import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.stmt.PreparedQuery
import com.j256.ormlite.support.ConnectionSource
import com.yingenus.pocketchinese.data.local.sqlite.entity.Key


class KeyDaoImpl(connectionSource: ConnectionSource) : BaseDaoImpl<Key,String>(connectionSource,Key::class.java),
    KeyDao {
    //@Query("SELECT * FROM zi_key")
    override fun getAll(): List<Key>{
        return queryForAll()
    }
    //@Query("SELECT * FROM zi_key WHERE radical = :radical")
    override fun loadByRadical( radical : String): List<Key>{
        val preparedQuery : PreparedQuery<Key> =
            queryBuilder().where().eq("radical",radical).prepare()

        return query(preparedQuery)
    }
}
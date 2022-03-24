package com.yingenus.pocketchinese.data.local.sqlite.dao
import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.stmt.PreparedQuery
import com.j256.ormlite.support.ConnectionSource
import com.yingenus.pocketchinese.data.local.sqlite.entity.ExampleLink


class ExampleLinkDaoImpl(connectionSource: ConnectionSource) : BaseDaoImpl<ExampleLink, String>(connectionSource, ExampleLink::class.java) ,
    ExampleLinkDao {
    //@Query("SELECT * FROM links")
    override fun loadAll(): List<ExampleLink>{
        return queryForAll()
    }
    //@Query("SELECT * FROM links WHERE word_id = :wordId")
    override fun loadByWordId( wordId : Int): ExampleLink?{
        val preparedQuery : PreparedQuery<ExampleLink> =
            queryBuilder().where().eq("word_id", wordId).prepare()

        val result = query(preparedQuery)
        return result.firstOrNull()
    }
}
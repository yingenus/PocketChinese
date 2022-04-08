package com.yingenus.pocketchinese.data.local

import com.yingenus.pocketchinese.data.local.sqlite.ExamplesDBHelper
import com.yingenus.pocketchinese.data.local.sqlite.dao.ExampleDao
import com.yingenus.pocketchinese.data.local.sqlite.dao.ExampleDaoImpl
import com.yingenus.pocketchinese.data.local.sqlite.dao.ExampleLinkDao
import com.yingenus.pocketchinese.data.local.sqlite.dao.ExampleLinkDaoImpl
import com.yingenus.pocketchinese.domain.dto.Example
import com.yingenus.pocketchinese.domain.repository.ExampleRepository
import javax.inject.Inject

class SqliteExampleRepository @Inject constructor(examplesHelper: ExamplesDBHelper) : ExampleRepository {

    private val exampleDao : ExampleDao = ExampleDaoImpl(examplesHelper.connectionSource)
    private val exampleLinkDao : ExampleLinkDao = ExampleLinkDaoImpl(examplesHelper.connectionSource)



    override fun findById(id: Int): Example? {
        return exampleDao.loadById(id)?.toExample()
    }

    override fun fundByChinCharId(id: Int): List<Example> {
        //return examplesDb.exampleDao().loadByWordId(id).map { it.toExample() }
        return fidByWordId(id, -1)
    }

    override fun fundByChinCharId(id: Int, maxSize: Int): List<Example> {
        //return examplesDb.exampleDao().loadByWordIdLimited(id,maxSize).map { it.toExample() }
        return fidByWordId(id,maxSize)
    }

    private fun fidByWordId(id : Int, maxSize: Int): List<Example>{

        val exampleLink = exampleLinkDao.loadByWordId(id)
        val links = exampleLink?.exampleIds?.split("__,__")?.map { it.toInt() }?: emptyList()

        return if (maxSize >= 0){
            val subLinks = if (links.size > maxSize) links.subList(0,maxSize) else links
            val exampleDao = exampleDao

            subLinks.mapNotNull { exampleDao.loadById(it)?.toExample() }
        }
        else{
            emptyList()
        }
    }
}
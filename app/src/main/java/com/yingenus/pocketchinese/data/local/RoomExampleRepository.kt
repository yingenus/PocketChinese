package com.yingenus.pocketchinese.data.local

import com.yingenus.pocketchinese.data.local.room.ExamplesDb
import com.yingenus.pocketchinese.domain.dto.Example
import com.yingenus.pocketchinese.domain.repository.ExampleRepository

class RoomExampleRepository( val examplesDb: ExamplesDb) : ExampleRepository {
    override fun findById(id: Int): Example? {
        return examplesDb.exampleDao().loadById(id)?.toExample()
    }

    override fun fundByChinCharId(id: Int): List<Example> {
        return examplesDb.exampleDao().loadByWordId(id).map { it.toExample() }
    }
}
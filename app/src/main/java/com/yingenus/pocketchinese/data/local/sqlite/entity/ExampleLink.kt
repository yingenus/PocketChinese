package com.yingenus.pocketchinese.data.local.sqlite.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "links")
class ExampleLink()
{
    @DatabaseField(id = true, columnName = "word_id")
    val wordId : Int? = null
    @DatabaseField(columnName = "exmpl_ids")
    val exampleIds : String? = null
}
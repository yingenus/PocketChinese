package com.yingenus.pocketchinese.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "links")
class ExampleLink(
    @PrimaryKey @ColumnInfo(name = "word_id") val wordId : Int,
    @ColumnInfo(name = "exmpl_ids") val exampleIds : String
)
{

}
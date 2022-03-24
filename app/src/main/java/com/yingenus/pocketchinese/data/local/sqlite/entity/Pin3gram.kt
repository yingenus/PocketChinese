package com.yingenus.pocketchinese.data.local.sqlite.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "pin_3grams")
class Pin3gram ()
{

    @DatabaseField(uniqueCombo = true,id = true, columnName = "ngram", index = true, indexName = "pin_3g_index")
    val ngram : String? = null
    @DatabaseField(uniqueCombo = true, columnName = "position", index = true, indexName = "pin_3g_index")
    val position : Int? = null
    @DatabaseField(columnName = "words")
    val wordsId : String? = null

    val wordsIds
        get() = wordsId!!.split(",").map { it.toInt() }
}
package com.yingenus.pocketchinese.data.local.sqlite.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "rus_3grams")
class Rus3gram (

        )
{

    @DatabaseField(id = true,uniqueCombo = true, columnName = "ngram", index = true, indexName = "rus_3g_undex")
    val ngram : String? = null
    @DatabaseField(uniqueCombo = true, columnName = "position", index = true, indexName = "rus_3g_undex" )
    val position : Int? = null
    @DatabaseField(columnName = "words")
    val wordsId : String? = null

    val wordsIds
        get() = wordsId!!.split(",").map { it.toInt() }

}
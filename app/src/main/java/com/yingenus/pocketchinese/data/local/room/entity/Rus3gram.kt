package com.yingenus.pocketchinese.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "rus_3grams",
        primaryKeys = ["ngram","position"],
        indices =[Index(
                name = "rus_3g_undex",
                value = ["ngram","position"]
        )] )
class Rus3gram (
        @ColumnInfo(name = "ngram") val ngram : String,
        @ColumnInfo(name = "position") val position : Int,
        @ColumnInfo(name = "words") val wordsId : String
        )
{

    val wordsIds
        get() = wordsId.split(",").map { it.toInt() }

}
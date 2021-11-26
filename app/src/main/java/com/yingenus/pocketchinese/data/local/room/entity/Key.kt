package com.yingenus.pocketchinese.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "zi_key", primaryKeys = [ "zi" , "spelling" ])
class Key(
    @ColumnInfo(name = "radical") val radical : String,
    @ColumnInfo(name = "zi") val character : String,
    @ColumnInfo(name = "spelling") val pinyin : String,
    @ColumnInfo(name = "stroke") val stroke : Int
) {
}
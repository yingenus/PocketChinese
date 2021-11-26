package com.yingenus.pocketchinese.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "zi_radical")
class Radical(
    @ColumnInfo(name = "lines") val stoke : String,
    @PrimaryKey @ColumnInfo(name = "radical") val radical : String
) {
}
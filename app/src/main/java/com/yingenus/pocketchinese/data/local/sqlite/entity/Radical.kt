package com.yingenus.pocketchinese.data.local.sqlite.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "zi_radical")
class Radical() {
    @DatabaseField(columnName = "lines")
    val stoke : String? = null
    @DatabaseField(id = true, columnName = "radical")
    val radical : String? = null
}
package com.yingenus.pocketchinese.data.local.sqlite.entity

import androidx.annotation.ColorInt
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import com.yingenus.pocketchinese.domain.dto.ZiChar

@DatabaseTable(tableName = "zi_key")
class Key(

) {
    @DatabaseField(columnName = "radical")
    val radical : String? = null
    @DatabaseField(uniqueCombo = true, id = true, columnName = "zi")
    val character : String? = null
    @DatabaseField(uniqueCombo = true, columnName = "spelling")
    val pinyin : String? = null
    @DatabaseField(columnName = "stroke")
    val stroke : Int? = null
    @DatabaseField(columnName = "in_db")
    val isInDB : Boolean? = null


    fun toZiChar(): ZiChar =
            ZiChar(
                    radical = radical!!,
                    character = character!!,
                    stroke = stroke!!,
                    isInDb = isInDB!!
            )
}
package com.yingenus.pocketchinese.data.local.sqlite.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import com.yingenus.pocketchinese.domain.dto.Example

@DatabaseTable(tableName = "examples")
class Example() {

        @DatabaseField(id = true, columnName = "id", canBeNull = false)
        val id : Int? =null
        @DatabaseField(columnName = "chinese_word", canBeNull = false)
        val chinese : String? =null
        @DatabaseField(columnName = "pinyin", canBeNull = false)
        val pinyin : String? =null
        @DatabaseField(columnName = "translation", canBeNull = false)
        val translation : String? =null
        @DatabaseField(columnName = "entry_words", canBeNull = true)
        val entry : String? =null

        fun toExample() = Example(
                id = id!!.toInt(),
                chinese = chinese!!,
                pinyin = pinyin!!,
                translation = translation!!
        )
}
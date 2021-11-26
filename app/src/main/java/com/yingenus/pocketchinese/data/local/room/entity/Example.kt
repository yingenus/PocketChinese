package com.yingenus.pocketchinese.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yingenus.pocketchinese.domain.dto.Example

@Entity(tableName = "examples")
class Example(
        @PrimaryKey val id : Int,
        @ColumnInfo(name = "chinese_word") val chinese : String,
        @ColumnInfo(name = "pinyin") val pinyin : String,
        @ColumnInfo(name = "translation") val translation : String,
        @ColumnInfo(name = "entry_words") val entry : String?
) {

        fun toExample() = Example(
                id = id.toInt(),
                chinese = chinese,
                pinyin = pinyin,
                translation = translation
        )
}
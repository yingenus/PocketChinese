package com.yingenus.pocketchinese.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yingenus.pocketchinese.domain.dto.Tone

@Entity(tableName = "py_variants", primaryKeys = [ "py", "tone" ])
class PinVariants(
    @ColumnInfo(name = "py") val pinyin : String,
    @ColumnInfo(name = "tone") val tone : String
) {
    fun toTone() = Tone(pinyin,tone)
}
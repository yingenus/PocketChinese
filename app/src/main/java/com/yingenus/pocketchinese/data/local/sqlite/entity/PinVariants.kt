package com.yingenus.pocketchinese.data.local.sqlite.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import com.yingenus.pocketchinese.domain.dto.Tone


@DatabaseTable(tableName = "py_variants")
class PinVariants() {

    @DatabaseField(id = true, columnName = "py")
    val pinyin : String? = null
    @DatabaseField(uniqueCombo = true, columnName = "tone")
    val tone : String? = null

    fun toTone() = Tone(pinyin!!,tone!!)
}
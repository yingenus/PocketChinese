package com.yingenus.pocketchinese.data.local.sqlite.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import com.yingenus.pocketchinese.domain.dto.UnitWord
import com.yingenus.pocketchinese.domain.dto.VariantWord

@DatabaseTable(tableName = "rus_words")
class RusWord(

){
    @DatabaseField(id = true, columnName = "word_id")
    val id: Int? = null
    @DatabaseField(columnName = "word")
    val word: String? = null
    @DatabaseField(columnName = "sents")
    val sentences: String? = null

    fun toUnitWord(): UnitWord {
        val variants = sentences!!.split(",").mapNotNull { getVariantWord(it) }

        return UnitWord(id!!, word!!, variants)
    }

    private fun getVariantWord(item: String): VariantWord {
        val weight = item.substring(item.indexOf("_") + 1).toInt()
        val idPlus = item.substring(0, item.indexOf("_")).toInt()
        val id = idPlus shr 10
        val index = idPlus and 1023
        return VariantWord(id, index, weight)
    }

}
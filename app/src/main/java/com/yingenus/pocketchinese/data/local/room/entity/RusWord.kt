package com.yingenus.pocketchinese.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yingenus.pocketchinese.domain.dto.UnitWord
import com.yingenus.pocketchinese.domain.dto.VariantWord


@Entity(tableName = "rus_words")
class RusWord(
    @PrimaryKey @ColumnInfo(name = "word_id") val id: Int,
    @ColumnInfo(name = "word") val word: String,
    @ColumnInfo(name = "sents") val sentences: String
){


    fun toUnitWord(): UnitWord {
        val variants = sentences.split(",").mapNotNull { getVariantWord(it) }

        return UnitWord(id, word, variants)
    }

    private fun getVariantWord(item: String): VariantWord {
        val weight = item.substring(item.indexOf("_") + 1).toInt()
        val idPlus = item.substring(0, item.indexOf("_")).toInt()
        val id = idPlus shr 10
        val index = idPlus and 1023
        return VariantWord(id, index, weight)
    }

}
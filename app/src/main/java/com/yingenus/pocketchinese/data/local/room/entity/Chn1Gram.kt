package com.yingenus.pocketchinese.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.yingenus.pocketchinese.domain.dto.VariantWord

@Entity(tableName = "chin_1gram",
        indices = [Index(
                name = "chn_1n_index",
                value = ["ngram"])])
class Chn1Gram (
        @PrimaryKey @ColumnInfo(name = "ngram")val ngram : String,
        @ColumnInfo(name = "sents") val sentences : String
    )
{
    val wordVariants : List<VariantWord>
        get() = sentences.split(",").map { toVariantWord(it) }

    private fun toVariantWord(item: String): VariantWord {
        val weight = item.substring(item.indexOf("_") + 1).toInt()
        val idPlus = item.substring(0, item.indexOf("_")).toInt()
        val id = idPlus shr 10
        val index = idPlus and 1023
        return VariantWord(id, index, weight)
    }
}
package com.yingenus.pocketchinese.data.local.sqlite.entity

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import com.yingenus.pocketchinese.domain.dto.VariantWord

@DatabaseTable(tableName = "chin_2gram")
class Chn2Gram ()
{

    @DatabaseField(id = true, columnName = "ngram", index = true, indexName = "chin_2g_index")
    val ngram : String? = null
    @DatabaseField(columnName = "sents")
    val sentences : String? = null

    val wordVariants : List<VariantWord>
        get() = sentences!!.split(",").map { toVariantWord(it) }

    private fun toVariantWord(item: String): VariantWord {
        val weight = item.substring(item.indexOf("_") + 1).toInt()
        val idPlus = item.substring(0, item.indexOf("_")).toInt()
        val id = idPlus shr 10
        val index = idPlus and 1023
        return VariantWord(id, index, weight)
    }
}
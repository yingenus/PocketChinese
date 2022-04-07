package com.yingenus.pocketchinese.data.local.sqlite.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import com.yingenus.pocketchinese.domain.dto.DictionaryItem

@Entity(tableName = "words")
@DatabaseTable(tableName = "words")
class Word() {

    @DatabaseField(id = true, columnName = "ID")
    val id: Int? = null
    @DatabaseField(columnName = "chinese_word")
    val chinese: String? = null
    @DatabaseField(columnName = "chinese_old")
    val chineseOld: String? = null
    @DatabaseField(columnName = "pinyin")
    val pinyin: String? = null
    @DatabaseField(columnName = "translation")
    val translation: String? = null
    @DatabaseField(columnName = "trn_examples")
    val examples: String? = null
    @DatabaseField(columnName = "tags")
    val tags: String? = null
    @DatabaseField(columnName = "indx_pt")
    val index_pinTrn : Boolean? = null


    fun toChinChar(): DictionaryItem{

        val translations = translation!!.string2Array()
        val tags = tags?.string2Array()

        val generalTag = tags?.getOrNull(0)?: ""

        val specialTag = Array<String>(translations.size){ index ->
            tags?.getOrNull(index) ?: ""
        }

        return DictionaryItem(
            id = id!!,
            chinese = chinese!!,
            pinyin = pinyin!!,
            translation = translations,
            generalTag = generalTag,
            specialTag = specialTag,
            chineseOld = chineseOld
        )
    }


}
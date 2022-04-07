package com.yingenus.pocketchinese.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yingenus.pocketchinese.domain.dto.DictionaryItem


@Entity(tableName = "words")
class Word(
    @PrimaryKey @ColumnInfo(name = "ID") val id: Int,
    @ColumnInfo(name = "chinese_word") val chinese: String,
    @ColumnInfo(name = "chinese_old") val chineseOld: String?,
    @ColumnInfo(name = "pinyin") val pinyin: String,
    @ColumnInfo(name = "translation") val translation: String,
    @ColumnInfo(name = "trn_examples") val examples: String?,
    @ColumnInfo(name = "tags") val tags: String?,
    @ColumnInfo(name = "indx_pt") val index_pinTrn : Boolean
) {


    fun toChinChar(): DictionaryItem{

        val translations = translation.string2Array()
        val tags = tags?.string2Array()

        val generalTag = tags?.getOrNull(0)?: ""

        val specialTag = Array<String>(translations.size){ index ->
            tags?.getOrNull(index) ?: ""
        }

        return DictionaryItem(
            id = id,
            chinese = chinese,
            pinyin = pinyin,
            translation = translations,
            generalTag = generalTag,
            specialTag = specialTag,
            chineseOld = chineseOld
        )
    }


}
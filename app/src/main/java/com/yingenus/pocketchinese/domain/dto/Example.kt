package com.yingenus.pocketchinese.domain.dto

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

class Example(
    val id : Int,
    val chinese : String,
    val pinyin : String,
    val translation : String
) {
}
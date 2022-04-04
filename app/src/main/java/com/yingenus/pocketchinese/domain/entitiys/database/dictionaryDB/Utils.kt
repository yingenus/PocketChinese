package com.yingenus.pocketchinese.domain.entitiys.database.dictionaryDB

internal const val SEPARATOR = "__,__"
internal const val EMPTY = "NULL"

internal fun string2Array(str: String): Array<String>{
    return str.split(SEPARATOR).map {if (it.equals("NULL")) "" else it }.toTypedArray()
}

internal fun array2String(array: Array<String>):String{
    return array.reduce { acc, s ->
        acc+ SEPARATOR +if(s.isNullOrBlank() || s.isEmpty() || s.isBlank()) EMPTY else s }
}
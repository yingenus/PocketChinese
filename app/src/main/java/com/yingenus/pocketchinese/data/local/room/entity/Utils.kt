package com.yingenus.pocketchinese.data.local.room.entity

internal const val SEPARATOR = "__,__"
internal const val EMPTY = "NULL"

internal fun String.string2Array(): Array<String>{
    return this.split(SEPARATOR).map {if (it.equals("NULL")) "" else it }.toTypedArray()
}

internal fun string2ArrayNoNULL(str: String): Array<String>{
    return str.split(SEPARATOR).toTypedArray()
}

internal fun array2String(array: Array<String>):String{
    return array.reduce { acc, s -> acc+ SEPARATOR+s }
}
package com.yingenus.pocketchinese.data.local.sqlite.entity

internal const val SEPARATOR = "__,__"
internal const val EMPTY = "NULL"

internal fun String.string2Array(): Array<String>{
    return this.split(SEPARATOR).map {if (it.equals("NULL")) "" else it }.toTypedArray()
}
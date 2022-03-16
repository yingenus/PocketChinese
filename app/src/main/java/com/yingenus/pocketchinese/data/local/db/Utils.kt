package com.yingenus.pocketchinese.data.local.db

fun ByteArray.toInt() : Int{
    require(this.size <= 4)
    var num : Int = 0
    this.forEachIndexed{ index: Int, byte: Byte ->
        num += byte.toInt() shl (8 * index - 1)
    }
    return num
}
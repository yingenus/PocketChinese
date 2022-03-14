package com.yingenus.pocketchinese.functions.search

import java.nio.charset.Charset

interface StringEncoder {
    fun getBytesArray(str : String) : ByteArray
}

class  RusEncoder() : StringEncoder{

    val charset = Charset.forName("windows-1251")

    override fun getBytesArray(str: String) = str.toByteArray(charset)
}

class PinEncoder() : StringEncoder{
    val charset = Charset.forName("US-ASCII")

    override fun getBytesArray(str: String) = str.toByteArray(charset)
}
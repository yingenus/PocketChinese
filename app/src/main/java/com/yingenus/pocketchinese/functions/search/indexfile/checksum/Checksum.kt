package com.yingenus.pocketchinese.functions.search.indexfile.checksum

interface Checksum{
    fun checksumLength(): Int
    fun write(b: ByteArray)
    fun flush()
    fun getChecksum(): ByteArray
}
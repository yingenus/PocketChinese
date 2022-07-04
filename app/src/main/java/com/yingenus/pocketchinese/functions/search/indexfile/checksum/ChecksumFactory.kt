package com.yingenus.pocketchinese.functions.search.indexfile.checksum

interface ChecksumFactory {
    fun createChecksum(): Checksum
}
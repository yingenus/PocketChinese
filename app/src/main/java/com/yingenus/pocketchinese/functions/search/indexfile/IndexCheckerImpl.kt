package com.yingenus.pocketchinese.functions.search.indexfile

import com.yingenus.pocketchinese.functions.search.indexfile.checksum.Checksum
import com.yingenus.pocketchinese.functions.search.indexfile.checksum.ChecksumFactory
import java.io.File

class IndexCheckerImpl( private val checksumFactory: ChecksumFactory): IndexChecker {

    override fun checkIndexFile(file : File): Boolean{
        val checksum : Checksum = checksumFactory.createChecksum()

        require(file.exists()) { "no such file" }

        val ips = file.inputStream()

        val bufferSize = 512
        val byteBuffer : ByteArray  = ByteArray(bufferSize)
        var readBytes : Int = 0
        var expectedChecksum : ByteArray = ByteArray(4)
        try {
            while ( ips.read(byteBuffer).also { readBytes = it } != -1){

                val subBuffer = if (readBytes == bufferSize && ips.available() > 0) {
                    byteBuffer
                }else{
                    expectedChecksum = byteBuffer.copyOfRange(readBytes-checksum.checksumLength(), readBytes)
                    byteBuffer.copyOfRange(0,readBytes - checksum.checksumLength())
                }

                checksum.write(subBuffer)
            }

            checksum.flush()

            return compere(expectedChecksum, checksum.getChecksum())

        }finally {
            ips.close()
        }

    }

    private fun compere(byteArray1: ByteArray, byteArray2: ByteArray): Boolean{
        require(byteArray1.size == byteArray2.size, { "byte arrays lenth suddently not a same" })
        byteArray1.indices.forEach { index ->
            if ( byteArray1[index] != byteArray2[index] ) return false
        }
        return true
    }
}
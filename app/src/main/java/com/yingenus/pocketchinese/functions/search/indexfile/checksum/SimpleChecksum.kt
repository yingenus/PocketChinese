package com.yingenus.pocketchinese.functions.search.indexfile.checksum

import kotlin.experimental.xor

class SimpleChecksum() : Checksum {

    companion object{
        private val length : Int = 4


    }
    // контрольная сумма из 4 байт
    private var checksum : ByteArray = ByteArray(4)
    // временный буфер
    private val tmpBuffer : ByteArray = ByteArray(4)
    //показывает первый пустой элемент буфера
    private var pointer: Int = 0

    override fun checksumLength(): Int  = length

    override fun flush() {
        if (pointer > 0){
            val subBuffer = tmpBuffer.copyOfRange(0,pointer)
            val flushBuffer = ByteArray(4)
            subBuffer.indices.forEach { index ->
                flushBuffer[index] = subBuffer[index]
            }
            addToChecksum(flushBuffer)
        }
    }

    override fun write(b: ByteArray) {
        val buffer = ByteArray(pointer + b.size)

        if (pointer>0) tmpBuffer.copyOfRange(0,pointer).forEachIndexed { index, byte -> buffer[index] = byte }
        b.forEachIndexed{ index, byte -> buffer[pointer+index] = byte }

        for (i in 0..(buffer.size / 4)){
            val start = i*4
            val expEnd = i*4+4

            if (expEnd <= buffer.size){
                val subBuffer = buffer.copyOfRange(start,expEnd)
                addToChecksum(subBuffer)
            }
            else{
                val subBuffer = buffer.copyOfRange(start,buffer.size)
                subBuffer.indices.forEach { index ->
                    tmpBuffer[index] = subBuffer[index]
                }
                pointer = subBuffer.lastIndex+1
                break
            }
            pointer = 0
        }
    }

    override fun getChecksum(): ByteArray  = checksum

    private fun addToChecksum(b : ByteArray){
        checksum.indices.forEach { index ->
            checksum[index] = checksum[index].xor(b[index])
        }
    }


}

class SimpleChecksumFactory() : ChecksumFactory {
    override fun createChecksum(): Checksum = SimpleChecksum()
}
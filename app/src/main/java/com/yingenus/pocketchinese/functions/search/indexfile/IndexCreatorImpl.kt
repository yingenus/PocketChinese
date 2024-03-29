package com.yingenus.pocketchinese.functions.search.indexfile

import com.yingenus.pocketchinese.domain.dto.UnitWord
import com.yingenus.pocketchinese.domain.dto.VariantWord
import com.yingenus.pocketchinese.functions.search.UnitWordIterator
import com.yingenus.pocketchinese.functions.search.indexfile.checksum.Checksum
import com.yingenus.pocketchinese.functions.search.indexfile.checksum.ChecksumFactory
import java.io.File
import java.io.OutputStream
import java.nio.charset.Charset

class IndexCreatorImpl( private val checksumFactory: ChecksumFactory) : IndexCreator {

    private data class WordLink(val word : String, val data : List<VariantWordLink>)
    private class VariantWordLink(val byteArray: ByteArray)

    companion object{

        private fun UnitWord.toWordLink(): WordLink {

            val variantsLink = this.mentions.map { it.toVariantWordLink() }

            return WordLink(this.word, variantsLink)
        }

        private fun VariantWord.toVariantWordLink(): VariantWordLink {
            val idPlus_2:Int = this.id shl 10
            val idPlus:Int = idPlus_2 + this.index;
            val weight:Short = this.weight.toShort();

            val array : ByteArray = toByteArray(idPlus) + toByteArray(
                weight,
                size = 2
            );

            return VariantWordLink(array)
        }

        private fun toByteArray(data : Number,size : Int = 4): ByteArray =
            ByteArray(size){index -> (data.toLong() shr (index * 8)).toByte()}.reversedArray()
        private fun toCharArray(data : Number,size : Int = 4): CharArray =
            CharArray(size){index -> (data.toLong() shr (index * 8)).toChar()}
    }


    override fun createIndex(file: File, charset: Charset, iterator: UnitWordIterator) {

        require(file.isFile)
        require(file.canWrite())

        val out = file.outputStream()
        val indexW = IndexedOutPut(out,checksumFactory.createChecksum());

        iterator.forEach { it ->

            val word = it.toWordLink()

            //write word length to the file 2 byte
            indexW.write(toByteArray(word.word.length, 2))
            //write word
            //word.word.format()
            indexW.write(word.word.toByteArray(charset))
            val startIndex = indexW.position + 8
            //write variants words data
            indexW.write(toByteArray(startIndex, 4))
            //write length of variants word
            indexW.write(toByteArray(word.data.size * 6))
            //write variants word
            word.data.forEach {
                indexW.write(it.byteArray)
            }
        }

        indexW.flush()
        indexW.close()
        out.close()
    }

    class IndexedOutPut (val ops : OutputStream, private val checksum: Checksum) : OutputStream(){

        var position : Int =0
            private set

        override fun close() {
            ops.close()
        }

        override fun flush() {
            write(ubyteArrayOf(0xFFu, 0xFFu).toByteArray())
            checksum.flush()
            ops.write(checksum.getChecksum())
            ops.flush()
        }

        override fun write(b: Int) {
            write(ByteArray(4){index -> (b.toLong() shr (index * 8)).toByte()})
        }

        override fun write(b: ByteArray) {
            write(b,0,b.size)
        }

        override fun write(b: ByteArray, off: Int, len: Int) {
            val subBuf : ByteArray = b.copyOfRange(off,len)
            position += subBuf.size
            writeToChecksum(subBuf,0,subBuf.size)
            ops.write(subBuf,0,subBuf.size)
        }

        private fun writeToChecksum(b : ByteArray, off: Int, len : Int){
            val subBuf : ByteArray = b.copyOfRange(off,len)
            checksum.write(subBuf)
        }
    }
}
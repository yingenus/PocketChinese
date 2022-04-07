package com.yingenus.pocketchinese.data.local.db

import android.content.Context
import java.io.File
import java.nio.ByteBuffer

class AppDatabaseVersionChecker : DbVersionChecker {
    override fun getVersion(context: Context, fileName: String): Int {
        val file = context.getDatabasePath(fileName)
        require(file.isFile)
        val ips = file.inputStream()
        val chn = ips.channel
        try {
            chn.tryLock(60,4,true)
            val buffer = ByteBuffer.allocate(4)
            chn.position(60)
            val read = chn.read(buffer)
            if (read != 4) throw Exception("smh wrong cant read database header")
            buffer.rewind()
            return buffer.int
        }finally {
            chn.close()
            ips.close()
        }
    }
}
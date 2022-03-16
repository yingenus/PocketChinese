package com.yingenus.pocketchinese.data.local.db

import android.content.Context
import android.content.res.AssetManager

class AssetsVersionChecker : DbVersionChecker {
    override fun getVersion(context: Context, file: String): Int {
        val ips = context.assets.open(file)

        try {
            val buffer = ByteArray(4)
            ips.skip(60)
            val read = ips.read(buffer, 0, 4)
            if (read != 4) throw Exception("smh wrong cant read database header")

            return buffer.toInt()
        } finally {
            ips.close()
        }
    }


}
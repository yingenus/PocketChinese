package com.yingenus.pocketchinese.data.assets

import android.content.Context
import java.net.URI

abstract class AssetsRepository(context: Context) {

    private val manager = context.assets

    protected abstract fun getFolder(): String

    fun getFileURI(name : String): URI?{
        val files =  manager.list(getFolder())

        if (files!!.contains(name)){
            return URI("file:///android_asset/${getFolder()}/$name")
        }
        return null
    }
}
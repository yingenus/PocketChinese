package com.yingenus.pocketchinese.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import java.net.URI

interface ImageRep {
    fun getImageURI(name : String) : URI?
    fun getImageUri(name : String) : Uri?
    fun getImageBitmap(name : String) : Bitmap?
}
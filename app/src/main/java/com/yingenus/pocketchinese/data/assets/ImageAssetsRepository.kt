package com.yingenus.pocketchinese.data.assets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.yingenus.pocketchinese.domain.repository.ImageRep
import java.io.File
import java.net.URI

class ImageAssetsRepository(val context: Context) : AssetsRepository(context), ImageRep {

    companion object{
        private const val folder = "image"
    }

    override fun getFolder() = folder

    override fun getImageURI(name: String) = getFileURI(name)

    override fun getImageUri(name: String): Uri? {
        return Uri.fromFile(File("//android_asset/$folder/$name"))
    }

    override fun getImageBitmap(name: String): Bitmap? {
        val inputStream = context.assets.open("$folder/$name")
        val bitmap=BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        return bitmap
    }
}
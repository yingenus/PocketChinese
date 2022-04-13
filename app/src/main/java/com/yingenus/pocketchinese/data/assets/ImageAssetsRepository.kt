package com.yingenus.pocketchinese.data.assets

import android.content.Context
import android.net.Uri
import com.yingenus.pocketchinese.domain.repository.ImageRep
import java.net.URI

class ImageAssetsRepository(context: Context) : AssetsRepository(context), ImageRep {

    companion object{
        private const val folder = "image"
    }

    override fun getFolder() = folder

    override fun getImageURI(name: String) = getFileURI(name)
}
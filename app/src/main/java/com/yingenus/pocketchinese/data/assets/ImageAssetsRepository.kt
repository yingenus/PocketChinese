package com.yingenus.pocketchinese.data.assets

import android.content.Context
import com.yingenus.pocketchinese.domain.repository.ImageRep
import java.net.URI

class ImageAssetsRepository(context: Context) : AssetsRepository(context), ImageRep {

    companion object{
        private const val folder = "image"
    }

    override fun getFolder() = folder

    override fun getImageUri(name: String) = getFileURI(name)
}
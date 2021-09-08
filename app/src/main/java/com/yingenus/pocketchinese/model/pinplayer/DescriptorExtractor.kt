package com.yingenus.pocketchinese.model.pinplayer

import android.content.Context
import java.io.IOException
import java.util.*

object DescriptorExtractor {
    private const val assetDir = "pinsound"
    private const val fileExtension = ".mp3"

    @Throws(IllegalArgumentException::class)
    fun getSoundDescriptor( tone : String, context: Context): ToneSoundDescriptor{

        val files = context.assets.list(assetDir) ?:
            throw IOException("cant find folder $assetDir")


        val file = files.find { it.removeSuffix(fileExtension) == tone }
        file ?: throw java.lang.IllegalArgumentException("cant find file ${tone+ fileExtension} in $assetDir")
        return ToneSoundDescriptor(tone, context.assets.openFd("$assetDir/$file"))
    }
}




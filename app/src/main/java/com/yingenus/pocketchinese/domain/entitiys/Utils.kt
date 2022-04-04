package com.yingenus.pocketchinese.domain.entitiys

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File
import java.io.IOException
import java.lang.RuntimeException
import kotlin.random.Random


const val imageDir = "image"

fun getBitmapFromAssets(context: Context,name : String):Bitmap?{
    return try {
        val inputStream = context.assets.open(name)
        val bitmap=BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        bitmap
    }catch (e:IOException){
        Log.e("image not find",e.message?:"no mes")
        null
    }
}

inline fun <reified E : Any> List<out E?>.randomize() : List<E>{
    val randomized = Array<E?>(this.size) { null }

    val freeIndex = randomized.mapIndexed { index, _ ->  index}.toMutableList()

    for (word in this){
        val insIndex = freeIndex
                .removeAt(Random.nextInt(freeIndex.size))

        if (randomized[insIndex] != null)
            throw RuntimeException("index is busy")

        randomized[insIndex] = word
    }

    return randomized.filterNotNull()
}

fun getFileFromAssets(resources: Context, fileName : String): File? {
    return try {
        val input = resources.assets.open(fileName)

        val name = fileName.substring(fileName.lastIndexOf("/")+1)
        //val prefix = name.substring(0  until  name.indexOf("."))
        //val suffux = name.substring(name.indexOf(".")+1)

        val outFile = File.createTempFile(name,null,resources.cacheDir)

        val output = outFile.outputStream()

        input.copyTo(output)
        input.close()
        output.flush()
        output.close()

        return outFile
    }catch (ioe : IOException){
        Log.e("file not find",ioe.localizedMessage)
        return null
    }
}
package com.yingenus.pocketchinese.data.json.suggest

import com.google.gson.GsonBuilder
import java.io.InputStream

object JSONHelper {
    private inline fun <reified T> loadJSON( ips : InputStream): T{
        val buider = GsonBuilder()
        buider.serializeNulls()
        val gson = buider.create()

        val reader = ips.reader()
        val jsonObject = gson.fromJson<T>(reader, T::class.java)

        reader.close()
        return jsonObject
    }

    fun loadDirInfo( ips : InputStream) = loadJSON<JSONObjects.DirInfo>(ips)

    fun loadWordList( ips : InputStream) = loadJSON<JSONObjects.WordList>(ips)
}
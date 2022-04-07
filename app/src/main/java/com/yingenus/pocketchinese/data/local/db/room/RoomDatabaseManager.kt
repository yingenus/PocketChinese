package com.yingenus.pocketchinese.data.local.db.room

import android.content.Context
import com.yingenus.pocketchinese.data.local.room.ExamplesDb
import com.yingenus.pocketchinese.data.local.room.WordsDb

interface RoomDatabaseManager {
    fun getDatabaseVersion(class_name : String, context: Context): Int
    fun getExampleDatabase(context: Context): ExamplesDb
    fun getWordsDatabase(context: Context): WordsDb
}
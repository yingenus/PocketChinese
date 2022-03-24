package com.yingenus.pocketchinese.data.local.db.sqlite

import android.content.Context
import com.yingenus.pocketchinese.data.local.room.ExamplesDb
import com.yingenus.pocketchinese.data.local.room.WordsDb
import com.yingenus.pocketchinese.data.local.sqlite.DictionaryDBHelper
import com.yingenus.pocketchinese.data.local.sqlite.ExamplesDBHelper

interface SqliteDatabaseManager {
    fun getDatabaseVersion(class_name : String, context: Context): Int
    fun getExampleDatabase(context: Context): ExamplesDBHelper
    fun getDictionaryDatabase(context: Context): DictionaryDBHelper
}
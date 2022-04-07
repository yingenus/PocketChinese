package com.yingenus.pocketchinese.data.local.db

import android.content.Context

interface DictionaryDatabaseVersion {
    fun getVersion(context: Context): Int
}
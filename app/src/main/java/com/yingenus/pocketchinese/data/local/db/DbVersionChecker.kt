package com.yingenus.pocketchinese.data.local.db

import android.content.Context

interface DbVersionChecker {
    fun getVersion( context: Context, file : String) : Int
}
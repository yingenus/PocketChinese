package com.yingenus.pocketchinese.model.database.pocketDB

import android.database.sqlite.SQLiteDatabase

open class PocketDAOs(db: SQLiteDatabase) {

    protected var database : SQLiteDatabase? = db

    fun finish(){
        database = null
    }

}
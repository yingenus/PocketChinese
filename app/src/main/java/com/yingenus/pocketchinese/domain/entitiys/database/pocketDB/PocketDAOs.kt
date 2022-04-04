package com.yingenus.pocketchinese.domain.entitiys.database.pocketDB

import android.database.sqlite.SQLiteDatabase

open class PocketDAOs(db: SQLiteDatabase) {

    protected var database : SQLiteDatabase? = db

    fun finish(){
        database = null
    }

}
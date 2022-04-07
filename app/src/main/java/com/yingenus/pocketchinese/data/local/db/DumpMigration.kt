package com.yingenus.pocketchinese.data.local.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class DumpMigration(from : Int, to : Int) :  Migration(from, to){
    override fun migrate(database: SupportSQLiteDatabase) {
        //now do nothing
    }
}
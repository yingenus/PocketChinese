package com.yingenus.pocketchinese.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yingenus.pocketchinese.data.local.room.entity.Example
import com.yingenus.pocketchinese.data.local.room.entity.ExampleLink

@Database(entities = [Example::class, ExampleLink::class], version = 5)
abstract class ExamplesDb : RoomDatabase() {
    abstract fun exampleDao(): ExampleDao
    abstract fun exampleLinkDao(): ExampleLinkDao
}
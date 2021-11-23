package com.yingenus.pocketchinese.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yingenus.pocketchinese.data.local.room.entity.Key
import com.yingenus.pocketchinese.data.local.room.entity.PinVariants
import com.yingenus.pocketchinese.data.local.room.entity.Radical
import com.yingenus.pocketchinese.data.local.room.entity.Word

@Database(entities = [Word::class, PinVariants::class, Key::class, Radical::class], version = 1)
abstract class WordsDb : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun radicalsDao(): RadicalsDao
    abstract fun keyDao(): KeyDao
    abstract fun variantsDao(): VariantsDao
}
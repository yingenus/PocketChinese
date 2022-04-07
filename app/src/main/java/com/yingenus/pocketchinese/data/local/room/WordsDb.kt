package com.yingenus.pocketchinese.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yingenus.pocketchinese.data.local.room.entity.*

@Database(entities = [Word::class, PinVariants::class, Key::class, Radical::class,
    Pin3gram::class, PinWord::class, Rus3gram::class, RusWord::class,
    Chn1Gram::class, Chn2Gram::class], version = 5)
abstract class WordsDb : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun radicalsDao(): RadicalsDao
    abstract fun keyDao(): KeyDao
    abstract fun variantsDao(): VariantsDao
    abstract fun pin3gramDao(): Pin3gramDao
    abstract fun rus3gramDao(): Rus3gramDao
    abstract fun pinWordDao(): PinWordDao
    abstract fun rusWordDao(): RusWordDao
    abstract fun chnNgramDao(): ChnNgramDao
}
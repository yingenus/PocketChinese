package com.yingenus.pocketchinese.di

import android.content.Context
import androidx.room.Room
import com.yingenus.pocketchinese.data.local.room.ExamplesDb
import com.yingenus.pocketchinese.data.local.room.PocketDb
import com.yingenus.pocketchinese.data.local.room.WordsDb
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule {

    @Provides
    @Singleton
    fun provideWordsDb( context : Context): WordsDb {
        return Room.databaseBuilder(context,WordsDb::class.java,"dictionaryDB.db")
            .createFromAsset("dictionaryDB.db")
            .fallbackToDestructiveMigration()
            .enableMultiInstanceInvalidation()
            .allowMainThreadQueries().build()
    }

    @Provides
    @Singleton
    fun provideExamplesDb(context: Context): ExamplesDb{
        return Room.databaseBuilder(context,ExamplesDb::class.java,"exampleDB.db")
            .createFromAsset("exampleDB.db")
            .fallbackToDestructiveMigration()
            .enableMultiInstanceInvalidation()
            .allowMainThreadQueries().build()
    }

    @Provides
    @Singleton
    fun providePocketDb(context : Context) : PocketDb{
        return Room.databaseBuilder(context,PocketDb::class.java,"learningPocket.db")
            .addMigrations(PocketDb.migration_3_4)
            .enableMultiInstanceInvalidation()
            .allowMainThreadQueries().build()
    }
}
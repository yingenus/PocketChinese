package com.yingenus.pocketchinese.di

import android.content.Context
import com.yingenus.pocketchinese.data.local.db.sqlite.InAssetsSqliteDatabaseManager
import com.yingenus.pocketchinese.data.local.db.sqlite.SqliteDatabaseManager
import com.yingenus.pocketchinese.data.local.sqlite.DictionaryDBHelper
import com.yingenus.pocketchinese.data.local.sqlite.ExamplesDBHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SQLiteModule {

    @Provides
    @Singleton
    fun provideDictionaryDb( databaseManager: SqliteDatabaseManager, context: Context) : DictionaryDBHelper{
        return  databaseManager.getDictionaryDatabase(context)
    }

    @Provides
    @Singleton
    fun provideExampleDb( databaseManager: SqliteDatabaseManager, context: Context) : ExamplesDBHelper{
        return databaseManager.getExampleDatabase(context)
    }

}
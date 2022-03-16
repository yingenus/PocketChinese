package com.yingenus.pocketchinese.data.local.db

import android.content.Context
import androidx.room.Room
import com.yingenus.pocketchinese.data.local.room.ExamplesDb
import com.yingenus.pocketchinese.data.local.room.WordsDb
import java.io.File
import java.io.FileOutputStream

class InAssetsDatabaseManager() : DatabaseManager {

    companion object{
        private const val ASSETS_EXAMPLE_DB_NAME = "exampleDB.db"
        private const val EXAMPLE_DB_NAME = "exampleDB.db"
        private const val ASSETS_WORDS_DB_NAME = "dictionaryDB.db"
        private const val WORDS_DB_NAME = "dictionaryDB.db"
    }


    private val versionCheckAssets : DbVersionChecker = AssetsVersionChecker()
    private val versionCheckFile : DbVersionChecker = AppDatabaseVersionChecker()

    private var examplesDb : Pair<Int,ExamplesDb>? = null
    private var wordsDb : Pair<Int,WordsDb>? = null

    override fun getDatabaseVersion(class_name : String, context: Context): Int=
        when (class_name){
            ExamplesDb::class.java.name -> (examplesDb?: initExampleDb(context)).first
            WordsDb::class.java.name -> (wordsDb?: initWordsDb(context)).first
            else -> throw IllegalAccessException("unsupported class : $class_name")
        }


    override fun getExampleDatabase(context: Context): ExamplesDb =
        (examplesDb?: initExampleDb(context)).second


    override fun getWordsDatabase( context: Context): WordsDb =
        (wordsDb?: initWordsDb(context)).second


    private fun initExampleDb(context: Context): Pair<Int,ExamplesDb>{
        val assetsVersion : Int = versionCheckAssets.getVersion(context, ASSETS_EXAMPLE_DB_NAME)

        val localDatabase : File = context.getDatabasePath(EXAMPLE_DB_NAME)

        val localVersion : Int =
            if ( localDatabase.exists() && localDatabase.length() > 0)
                versionCheckFile.getVersion(context, EXAMPLE_DB_NAME)
            else
                -1

        if (assetsVersion > localVersion){
            copyDB(context, EXAMPLE_DB_NAME, ASSETS_EXAMPLE_DB_NAME)
        }

        val builder = Room
            .databaseBuilder(context,ExamplesDb::class.java, EXAMPLE_DB_NAME)
            .enableMultiInstanceInvalidation()
            .allowMainThreadQueries()

        if (localVersion != 0 && assetsVersion > localVersion){
            builder.addMigrations(DumpMigration(localVersion,assetsVersion))
        }

        val roomDB = builder.build()

        val newExamplesDb = assetsVersion to roomDB
        examplesDb = newExamplesDb

        return newExamplesDb
    }

    private fun initWordsDb(context: Context): Pair<Int,WordsDb>{
        val assetsVersion : Int = versionCheckAssets.getVersion(context, ASSETS_WORDS_DB_NAME)

        val localDatabase : File = context.getDatabasePath(WORDS_DB_NAME)

        val localVersion : Int =
            if ( localDatabase.exists() && localDatabase.length() > 0)
                versionCheckFile.getVersion(context, WORDS_DB_NAME)
            else
                -1

        if (assetsVersion > localVersion){
            copyDB(context, WORDS_DB_NAME, ASSETS_WORDS_DB_NAME)
        }

        val builder = Room
            .databaseBuilder(context,WordsDb::class.java, WORDS_DB_NAME)
            .enableMultiInstanceInvalidation()
            .allowMainThreadQueries()

        if (localVersion != 0 && assetsVersion > localVersion){
            builder.addMigrations(DumpMigration(localVersion,assetsVersion))
        }

        val roomDB = builder.build()

        val newWordsDb = assetsVersion to roomDB
        wordsDb = newWordsDb

        return newWordsDb
    }


    private fun copyDB(context: Context, name : String, assets_name : String){


        val dbFile = context.getDatabasePath(name)!!

        if (!dbFile.exists()){
            val  path = dbFile.parentFile!!
            if (!path.exists()){
                path.mkdirs()
            }
            dbFile.createNewFile()

        }

        val dbAssets = context.assets.open(assets_name);
        val dbOutPut = FileOutputStream(dbFile)

        try {
            dbAssets.copyTo(dbOutPut, 1024)
        }finally {
            dbOutPut.flush()
            dbOutPut.close()
            dbAssets.close()
        }

    }
}
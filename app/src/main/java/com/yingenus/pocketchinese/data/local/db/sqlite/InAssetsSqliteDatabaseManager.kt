package com.yingenus.pocketchinese.data.local.db.sqlite

import android.content.Context
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.dao.DaoManager
import com.yingenus.pocketchinese.data.local.db.AppDatabaseVersionChecker
import com.yingenus.pocketchinese.data.local.db.AssetsVersionChecker
import com.yingenus.pocketchinese.data.local.db.DbVersionChecker
import com.yingenus.pocketchinese.data.local.db.DictionaryDatabaseVersion
import com.yingenus.pocketchinese.data.local.sqlite.DictionaryDBHelper
import com.yingenus.pocketchinese.data.local.sqlite.ExamplesDBHelper
import java.io.File
import java.io.FileOutputStream

class InAssetsSqliteDatabaseManager() : SqliteDatabaseManager, DictionaryDatabaseVersion {

    companion object{
        private const val ASSETS_EXAMPLE_DB_NAME = "exampleDB.db"
        private const val EXAMPLE_DB_NAME = "exampleDB.db"
        private const val ASSETS_WORDS_DB_NAME = "dictionaryDB.db"
        private const val WORDS_DB_NAME = "dictionaryDB.db"

        private fun assetsFileOf(class_name: String) = when(class_name){
            ExamplesDBHelper::class.java.name -> ASSETS_EXAMPLE_DB_NAME
            DictionaryDBHelper::class.java.name -> ASSETS_WORDS_DB_NAME
            else -> throw IllegalArgumentException()
        }

        private fun appFileOf(class_name: String) = when(class_name){
            ExamplesDBHelper::class.java.name -> EXAMPLE_DB_NAME
            DictionaryDBHelper::class.java.name -> WORDS_DB_NAME
            else -> throw IllegalArgumentException()
        }

    }


    private val versionCheckAssets : DbVersionChecker = AssetsVersionChecker()
    private val versionCheckFile : DbVersionChecker = AppDatabaseVersionChecker()

    private var examplesDbVersion : Int? = null
    private var examplesDbHelper : ExamplesDBHelper? = null
    private var dictionaryDbVersion : Int? = null
    private var dictionaryDbHelper : DictionaryDBHelper? = null

    override fun getVersion(context: Context): Int {
        return getDatabaseVersion(DictionaryDBHelper::class.java.name, context)
    }

    override fun isDatabaseExist(class_name: String, context: Context): Boolean {
        require(supportDatabase(class_name)) { "database class - ${class_name} is not supported" }

        val name : String = appFileOf(class_name)

        val localDatabase: File = context.getDatabasePath(name)

        return localDatabase.exists() && localDatabase.length() > 0
    }

    override fun isActualVersion(class_name: String, context: Context): Boolean {
        require(supportDatabase(class_name)) { "database class - ${class_name} is not supported" }

        val assetsName = assetsFileOf(class_name)

        val name : String = appFileOf(class_name)

        val assetsVersion : Int = versionCheckAssets.getVersion(context, assetsName)

        val localDatabase : File = context.getDatabasePath(name)

        val localVersion : Int =
            if ( localDatabase.exists() && localDatabase.length() > 0)
                versionCheckFile.getVersion(context, name)
            else
                -1

        return assetsVersion <= localVersion
    }

    override fun updateDatabase(class_name: String, context: Context) {
        require(supportDatabase(class_name)) { "database class - ${class_name} is not supported" }

        if (isActualVersion(class_name, context)) return

        copyDB(context, appFileOf(class_name), assetsFileOf(class_name))
    }

    override fun getDatabaseVersion(class_name : String, context: Context): Int=
        when (class_name){
            ExamplesDBHelper::class.java.name -> {
                if (examplesDbVersion == null){
                    val name = appFileOf(ExamplesDBHelper::class.java.name)
                    val database : File = context.getDatabasePath(name)
                    if ( database.exists() && database.length() > 0)
                        versionCheckFile.getVersion(context, name)
                    else
                        -1
                }
                else
                    examplesDbVersion!!
            }
            DictionaryDBHelper::class.java.name -> {
               if (dictionaryDbVersion == null){
                   val name = appFileOf(DictionaryDBHelper::class.java.name)
                   val database : File = context.getDatabasePath(name)
                   if ( database.exists() && database.length() > 0)
                       versionCheckFile.getVersion(context, name)
                   else
                       -1
               }
               else
                   dictionaryDbVersion!!
            }
            else -> throw IllegalAccessException("unsupported class : $class_name")
        }

    override fun getExampleDatabase(context: Context): ExamplesDBHelper {
        if (examplesDbHelper == null){
            examplesDbHelper = openDatabase(ExamplesDBHelper::class.java.name, context) as ExamplesDBHelper
        }
        return examplesDbHelper!!
    }

    override fun getDictionaryDatabase( context: Context): DictionaryDBHelper {
        if (dictionaryDbHelper == null){
            dictionaryDbHelper = openDatabase(DictionaryDBHelper::class.java.name, context) as DictionaryDBHelper
        }
        return dictionaryDbHelper!!
    }

    private fun openDatabase(class_name: String, context: Context): OrmLiteSqliteOpenHelper{
        require(supportDatabase(class_name)) { "database class - ${class_name} is not supported" }

        val name = appFileOf(class_name)

        val localDatabase : File = context.getDatabasePath(name)

        val localVersion : Int =
            if ( localDatabase.exists() && localDatabase.length() > 0)
                versionCheckFile.getVersion(context, name)
            else
                throw Exception("file of database - $class_name is not exist or was corrupted")

        val openHelper : OrmLiteSqliteOpenHelper =  when(class_name){
            ExamplesDBHelper::class.java.name ->
                ExamplesDBHelper(context,name, localVersion)
            DictionaryDBHelper::class.java.name ->
                DictionaryDBHelper(context,name, localVersion)
            else -> throw java.lang.IllegalArgumentException()
        }

        BaseDaoImpl.clearAllInternalObjectCaches()
        DaoManager.clearDaoCache()

        return openHelper
    }

    private fun initExampleDb(context: Context){
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

        val examplesDBHelper = ExamplesDBHelper(context,EXAMPLE_DB_NAME, assetsVersion)

        BaseDaoImpl.clearAllInternalObjectCaches()
        DaoManager.clearDaoCache()

        examplesDbVersion = assetsVersion
        examplesDbHelper = examplesDBHelper
    }
    private fun initDictionaryDb(context: Context){
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

        val dictionaryDBHelper = DictionaryDBHelper(context,WORDS_DB_NAME, assetsVersion)

        BaseDaoImpl.clearAllInternalObjectCaches()
        DaoManager.clearDaoCache()

        dictionaryDbHelper = dictionaryDBHelper
        dictionaryDbVersion = assetsVersion
    }

    private fun supportDatabase(class_name: String) : Boolean{
        return when(class_name){
            ExamplesDBHelper::class.java.name -> true
            DictionaryDBHelper::class.java.name -> true
            else -> false
        }
    }

    override fun close(){
        examplesDbHelper?.close()
        dictionaryDbHelper?.close()
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
package com.yingenus.pocketchinese.domain.entitiys.database

import android.content.Context
import android.database.SQLException
import android.util.Log
import com.yingenus.pocketchinese.data.local.sqlite.DictionaryDBHelper
import java.io.FileOutputStream
import java.io.IOException

internal const val SEPARATOR = "__,__"

internal fun string2Array(str: String): Array<String>{
    return str.split(SEPARATOR).toTypedArray()
}

internal fun array2String(array: Array<String>):String{
    return array.reduce { acc, s -> acc+ SEPARATOR+s }
}


class CopierDBs(val dbName: String){


    fun isExist(context: Context, newVersion : Int): Boolean{
        /*
        val dbList = context.databaseList()

        if ((dbList == null || !dbList.contains(dbName))){
            return false
        }else{

            try {
                val dbHelper =
                    DictionaryDBHelper(
                        context
                    )

                try {
                    val result = dbHelper.readableDatabase.query("metadata", null, "name = ?", arrayOf("version"), null, null, null)

                    if (result.count != 0) {
                        result.moveToFirst()
                        val strValue = result.getString(result.getColumnIndex("value")).replace(".","")
                        val oldVersion = strValue.toInt()

                        if (oldVersion >= newVersion) {
                            return true
                        }
                    }
                }finally {
                    dbHelper.close()
                }
            }catch (e : SQLException){
                Log.i("$dbName ", "db not have  metadata table")
            }

        }

         */
        return false
    }

    fun copyDB(context: Context): Boolean{

        try {
            val dbFile = context.getDatabasePath(dbName)!!

            if (!dbFile.exists()){
                val  path = dbFile.parentFile!!
                if (!path.exists()){
                    path.mkdirs()
                }
                dbFile.createNewFile()

            }

            val dbAssets = context.assets.open(dbName)
            val dbOutPut = FileOutputStream(dbFile)

            dbAssets.copyTo(dbOutPut, 1024)

            dbOutPut.flush()
            dbOutPut.close()
            dbAssets.close()

            return true
        }catch (IOE : IOException){
            Log.w("Copy db",IOE.message?:" ")
            return false
        }

    }



}

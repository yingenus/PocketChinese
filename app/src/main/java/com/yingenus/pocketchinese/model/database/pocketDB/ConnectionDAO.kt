package com.yingenus.pocketchinese.model.database.pocketDB

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import java.util.*

class ConnectionDAO(db: SQLiteDatabase): PocketDAOs(db) {

    fun create(connection: Connection){
        create(connection.listUUID,connection.wordUUID,connection.block)
    }

    fun update(connection: Connection){
        update(connection.listUUID,connection.wordUUID,connection.block)
    }

    fun remove(connection: Connection){
        remove(connection.listUUID,connection.wordUUID)
    }

    fun getAll(studyListUUID: UUID) = getAllIn( PocketDbSchema.StudyWordLinkTable.Cols.LIST_UUID, arrayOf(studyListUUID.toString()))

    fun getAll(): List<Connection>? {
        val cursor=queryConnectionTable(null,null)
        try {
            if (cursor.count==0)
                return Collections.emptyList()
            val list= mutableListOf<Connection>()
            cursor.moveToFirst()
            while (!cursor.isAfterLast){
                list.add(cursor.getConnection())
                cursor.moveToNext()
            }
            return list
        }finally {
            cursor.close()
        }
    }

    private fun create(studyListUUID: UUID, wordUUID: UUID, block: Int){
        database!!.insert(PocketDbSchema.StudyWordLinkTable.NAME, null,getContentValues(Connection(studyListUUID, wordUUID, block)))
    }
    private fun update(studyListUUID: UUID, wordUUID: UUID, block: Int){
        database!!.update(PocketDbSchema.StudyWordLinkTable.NAME,getContentValues(Connection(studyListUUID, wordUUID, block)),
                "${PocketDbSchema.StudyWordLinkTable.Cols.LIST_UUID} = ? AND ${PocketDbSchema.StudyWordLinkTable.Cols.WORDS_UUID} = ?",
                arrayOf(studyListUUID.toString(),wordUUID.toString()))
    }

    private fun remove(studyListUUID: UUID, wordUUID: UUID){
        database!!.delete(PocketDbSchema.StudyWordLinkTable.NAME,
                "${PocketDbSchema.StudyWordLinkTable.Cols.LIST_UUID} = ? AND ${PocketDbSchema.StudyWordLinkTable.Cols.WORDS_UUID} = ?",
                arrayOf(studyListUUID.toString(),wordUUID.toString()))
    }



    private fun getAllIn(wereCase:String?, wereArgs: Array<String>?): List<Connection>?{
        val cursor = queryConnectionTable("$wereCase = ?", wereArgs)

        try {
            if (cursor.count==0)
                return Collections.emptyList()

            val connections= mutableListOf<Connection>()

            cursor.moveToFirst()
            while (!cursor.isAfterLast){
                connections.add(cursor.getConnection())
                cursor.moveToNext()
            }
            return connections
        }finally {
            cursor.close()
        }
    }

    private fun queryConnectionTable(wereCase:String?, wereArgs: Array<String>?)=
            ConnectionCursorWrapper(database!!.query(PocketDbSchema.StudyWordLinkTable.NAME,
                    null, wereCase, wereArgs, null, null, null))


    private fun getContentValues(connection: Connection): ContentValues {
        val values= ContentValues()
        values.put(PocketDbSchema.StudyWordLinkTable.Cols.LIST_UUID,connection.listUUID.toString())
        values.put(PocketDbSchema.StudyWordLinkTable.Cols.WORDS_UUID,connection.wordUUID.toString())
        values.put(PocketDbSchema.StudyWordLinkTable.Cols.BLOCK,connection.block)

        return values
    }
}
package com.yingenus.pocketchinese.model.database.pocketDB

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import java.util.*

class StudyListDAO(db: SQLiteDatabase): PocketDAOs(db) {

    fun create(studyList: StudyList){
        database!!.insert(PocketDbSchema.StudyListTable.NAME,null,getContentValues(studyList))
    }

    fun getAll():List<StudyList>{
        val cursor=queryStudyList(null,null)
        try {
            if (cursor.count==0)
                return Collections.emptyList()
            val list= mutableListOf<StudyList>()
            cursor.moveToFirst()
            while (!cursor.isAfterLast){
                list.add(cursor.getStudyList())
                cursor.moveToNext()
            }
            return list
        }finally {
            cursor.close()
        }
    }

    fun get(name:String): StudyList? {
        val cursor=queryStudyList(
                PocketDbSchema.StudyListTable.Cols.NAME+" = ?", arrayOf(name))
        try{
            if(cursor.count == 0)
                return null
            cursor.moveToFirst()
            return cursor.getStudyList()
        }finally {
            cursor.close()
        }
    }

    fun get(uuid: UUID): StudyList? {
        val cursor=queryStudyList(
                PocketDbSchema.StudyListTable.Cols.UUID+" = ?", arrayOf(uuid.toString()))
        try{
            if(cursor.count==0)
                return null
            cursor.moveToFirst()
            return cursor.getStudyList()
        }finally {
            cursor.close()
        }
    }

    fun update(studyList: StudyList){
        val uuid=studyList.uuid.toString()
        database!!.update(PocketDbSchema.StudyListTable.NAME,getContentValues(studyList),
                PocketDbSchema.StudyListTable.Cols.UUID+" = ?", arrayOf(uuid))
    }

    fun remove(studyList: StudyList){
        remove(studyList.uuid)
    }
    fun remove(uuid: UUID){
        database!!.delete(PocketDbSchema.StudyListTable.NAME,
                PocketDbSchema.StudyListTable.Cols.UUID+" = ?", arrayOf(uuid.toString()))
    }


    private fun getContentValues(studyList: StudyList): ContentValues {
        val values= ContentValues()
        values.put(PocketDbSchema.StudyListTable.Cols.NAME,studyList.name)
        values.put(PocketDbSchema.StudyListTable.Cols.ITEMS,studyList.items)
        values.put(PocketDbSchema.StudyListTable.Cols.SUCCESS,studyList.success)
        values.put(PocketDbSchema.StudyListTable.Cols.WORST,studyList.worst)
        values.put(PocketDbSchema.StudyListTable.Cols.UPDATE_DATE,studyList.updateDate.time)
        values.put(PocketDbSchema.StudyListTable.Cols.LAST_REPEAT,studyList.lastRepeat.time)
        values.put(PocketDbSchema.StudyListTable.Cols.UUID,studyList.uuid.toString())
        values.put(PocketDbSchema.StudyListTable.Cols.NOTIFY,if(studyList.notifyUser) "true" else "false")

        return values
    }

    private fun queryStudyList(wereCase:String?,wereArgs: Array<String>?)=
            StudyListCursorWrapper(database!!.query(PocketDbSchema.StudyListTable.NAME,
                    null, wereCase, wereArgs, null, null, null))

}
package com.yingenus.pocketchinese.domain.entitiys.database.pocketDB

import android.database.Cursor
import android.database.CursorWrapper

import java.util.*

class StudyListCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {
    fun getStudyList(): StudyList {
        val name=getString(getColumnIndex(PocketDbSchema.StudyListTable.Cols.NAME))
        val items=getInt(getColumnIndex(PocketDbSchema.StudyListTable.Cols.ITEMS))
        val lastRep=getLong(getColumnIndex(PocketDbSchema.StudyListTable.Cols.LAST_REPEAT))
        val success=getInt(getColumnIndex(PocketDbSchema.StudyListTable.Cols.SUCCESS))
        val worst=getInt(getColumnIndex(PocketDbSchema.StudyListTable.Cols.WORST))
        val uuid=getString(getColumnIndex(PocketDbSchema.StudyListTable.Cols.UUID))
        val update=getLong(getColumnIndex(PocketDbSchema.StudyListTable.Cols.UPDATE_DATE))
        val notify = getString(getColumnIndex(PocketDbSchema.StudyListTable.Cols.NOTIFY)) == "true"

        return StudyList(name, items, null, Date(update), Date(lastRep), success, worst, UUID.fromString(uuid),notify)
    }
}
package com.yingenus.pocketchinese.model.database.pocketDB

import android.database.Cursor
import android.database.CursorWrapper
import java.util.*


class ConnectionCursorWrapper(cursor: Cursor): CursorWrapper(cursor) {
    fun getConnection(): Connection {
        val listUUID=getString(getColumnIndex(PocketDbSchema.StudyWordLinkTable.Cols.LIST_UUID))
        val wordUUID=getString(getColumnIndex(PocketDbSchema.StudyWordLinkTable.Cols.WORDS_UUID))
        val block=getInt(getColumnIndex(PocketDbSchema.StudyWordLinkTable.Cols.BLOCK))

        return Connection(UUID.fromString(listUUID), UUID.fromString(wordUUID), block)
    }
}
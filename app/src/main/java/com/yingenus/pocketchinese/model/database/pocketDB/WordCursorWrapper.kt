package com.yingenus.pocketchinese.model.database.pocketDB

import android.database.Cursor
import android.database.CursorWrapper
import java.util.*

class WordCursorWrapper(cursor: Cursor): CursorWrapper(cursor) {
    fun getWord(): StudyWord {
        val word =getString(getColumnIndex(PocketDbSchema.WordsTable.Cols.WORD))
        val pin=getString(getColumnIndex(PocketDbSchema.WordsTable.Cols.PINYIN))
        val transl=getString(getColumnIndex(PocketDbSchema.WordsTable.Cols.TRANSLATE))
        val uuid=getString(getColumnIndex(PocketDbSchema.WordsTable.Cols.UUID))
        val repeatw=getInt(getColumnIndex(PocketDbSchema.WordsTable.Cols.COMMON_REPEAT_W))
        val repeatp=getInt(getColumnIndex(PocketDbSchema.WordsTable.Cols.COMMON_REPEAT_P))
        val repeatt=getInt(getColumnIndex(PocketDbSchema.WordsTable.Cols.COMMON_REPEAT_T))
        val levelw=getInt(getColumnIndex(PocketDbSchema.WordsTable.Cols.LEVEL_W))
        val levelp=getInt(getColumnIndex(PocketDbSchema.WordsTable.Cols.LEVEL_P))
        val levelt=getInt(getColumnIndex(PocketDbSchema.WordsTable.Cols.LEVEL_T))
        val chnState=getInt(getColumnIndex(PocketDbSchema.WordsTable.Cols.REPEAT_STATE_W))
        val pinState=getInt(getColumnIndex(PocketDbSchema.WordsTable.Cols.REPEAT_STATE_P))
        val trnState=getInt(getColumnIndex(PocketDbSchema.WordsTable.Cols.REPEAT_STATE_T))
        val createDate=getLong(getColumnIndex(PocketDbSchema.WordsTable.Cols.CREATE_DATE))
        val cLastRep=getLong(getColumnIndex(PocketDbSchema.WordsTable.Cols.LAST_REPEAT_W))
        val pLastRep=getLong(getColumnIndex(PocketDbSchema.WordsTable.Cols.LAST_REPEAT_P))
        val tLastRep=getLong(getColumnIndex(PocketDbSchema.WordsTable.Cols.LAST_REPEAT_T))

        return StudyWord(word, pin, transl, repeatw, repeatp, repeatt, levelw, levelp, levelt
                , chnState, pinState, trnState, Date(cLastRep), Date(pLastRep), Date(tLastRep), Date(createDate), UUID.fromString(uuid))
    }
}
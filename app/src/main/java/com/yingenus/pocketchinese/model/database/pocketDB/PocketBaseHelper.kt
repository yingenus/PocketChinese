package com.yingenus.pocketchinese.model.database.pocketDB

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

val POCKET_DB_VERSION= 3
val POCKET_DB_NAME="learningPocket.db"

private const val STUDY_LIST_CREATE_ENTRY="CREATE TABLE ${PocketDbSchema.StudyListTable.NAME} ("+
        PocketDbSchema.StudyListTable.Cols.UUID +", "+
        PocketDbSchema.StudyListTable.Cols.NAME +", "+
        PocketDbSchema.StudyListTable.Cols.UPDATE_DATE +", "+
        PocketDbSchema.StudyListTable.Cols.ITEMS +", "+
        PocketDbSchema.StudyListTable.Cols.LAST_REPEAT +", "+
        PocketDbSchema.StudyListTable.Cols.SUCCESS +", "+
        PocketDbSchema.StudyListTable.Cols.WORST +", "+
        PocketDbSchema.StudyListTable.Cols.NOTIFY+ " DEFAULT 'true' "+
        ");"

private const val SW_LINK_CREATE_ENTRY="CREATE TABLE ${PocketDbSchema.StudyWordLinkTable.NAME} ("+
        PocketDbSchema.StudyWordLinkTable.Cols.LIST_UUID +", "+
        PocketDbSchema.StudyWordLinkTable.Cols.WORDS_UUID +", "+
        PocketDbSchema.StudyWordLinkTable.Cols.BLOCK +
        ");"

private const val WORDS_CREATE_ENTRY="CREATE TABLE ${PocketDbSchema.WordsTable.NAME} ("+
        PocketDbSchema.WordsTable.Cols.UUID +", "+
        PocketDbSchema.WordsTable.Cols.WORD +", "+
        PocketDbSchema.WordsTable.Cols.PINYIN +", "+
        PocketDbSchema.WordsTable.Cols.TRANSLATE +", "+
        PocketDbSchema.WordsTable.Cols.COMMON_REPEAT_W +", "+
        PocketDbSchema.WordsTable.Cols.COMMON_REPEAT_P +", "+
        PocketDbSchema.WordsTable.Cols.COMMON_REPEAT_T +", "+
        PocketDbSchema.WordsTable.Cols.LEVEL_W +", "+
        PocketDbSchema.WordsTable.Cols.LEVEL_P +", "+
        PocketDbSchema.WordsTable.Cols.LEVEL_T +", "+
        PocketDbSchema.WordsTable.Cols.LAST_REPEAT_W +", "+
        PocketDbSchema.WordsTable.Cols.LAST_REPEAT_P +", "+
        PocketDbSchema.WordsTable.Cols.LAST_REPEAT_T +", "+
        PocketDbSchema.WordsTable.Cols.CREATE_DATE +", "+
        PocketDbSchema.WordsTable.Cols.REPEAT_STATE_W +", "+
        PocketDbSchema.WordsTable.Cols.REPEAT_STATE_P +", "+
        PocketDbSchema.WordsTable.Cols.REPEAT_STATE_T +
        ");"

private const val STUDY_LIST_UPGRADE_1="DROP TABLE IF EXISTS ${PocketDbSchema.StudyListTable.NAME};"
private const val SW_LINK_UPGRADE_1="DROP TABLE IF EXISTS ${PocketDbSchema.StudyWordLinkTable.NAME};"
private const val WORDS_LIST_UPGRADE_1_1 = "ALTER TABLE ${PocketDbSchema.WordsTable.NAME} RENAME TO ${PocketDbSchema.WordsTable.NAME+"t"}"
private const val WORDS_LIST_UPGRADE_1_2="CREATE TABLE ${PocketDbSchema.WordsTable.NAME} ${"("+
        PocketDbSchema.WordsTable.Cols.UUID +", "+
        PocketDbSchema.WordsTable.Cols.WORD +", "+
        PocketDbSchema.WordsTable.Cols.PINYIN +", "+
        PocketDbSchema.WordsTable.Cols.TRANSLATE +", "+
        PocketDbSchema.WordsTable.Cols.COMMON_REPEAT_W +", "+
        PocketDbSchema.WordsTable.Cols.COMMON_REPEAT_P +", "+
        PocketDbSchema.WordsTable.Cols.COMMON_REPEAT_T +", "+
        PocketDbSchema.WordsTable.Cols.LEVEL_W +", "+
        PocketDbSchema.WordsTable.Cols.LEVEL_P +", "+
        PocketDbSchema.WordsTable.Cols.LEVEL_T +", "+
        PocketDbSchema.WordsTable.Cols.LAST_REPEAT_W +", "+
        PocketDbSchema.WordsTable.Cols.LAST_REPEAT_P +", "+
        PocketDbSchema.WordsTable.Cols.LAST_REPEAT_T +", "+
        PocketDbSchema.WordsTable.Cols.CREATE_DATE +", "+
        PocketDbSchema.WordsTable.Cols.REPEAT_STATE_W +", "+
        PocketDbSchema.WordsTable.Cols.REPEAT_STATE_P +", "+
        PocketDbSchema.WordsTable.Cols.REPEAT_STATE_T +
        ")"};"
private const val WORDS_LIST_UPGRADE_1_3 = "INSERT INTO ${PocketDbSchema.WordsTable.NAME}${"("+
        PocketDbSchema.WordsTable.Cols.UUID +", "+
        PocketDbSchema.WordsTable.Cols.WORD +", "+
        PocketDbSchema.WordsTable.Cols.PINYIN +", "+
        PocketDbSchema.WordsTable.Cols.TRANSLATE +", "+
        PocketDbSchema.WordsTable.Cols.COMMON_REPEAT_W +", "+
        PocketDbSchema.WordsTable.Cols.COMMON_REPEAT_P +", "+
        PocketDbSchema.WordsTable.Cols.COMMON_REPEAT_T +", "+
        PocketDbSchema.WordsTable.Cols.LEVEL_W +", "+
        PocketDbSchema.WordsTable.Cols.LEVEL_P +", "+
        PocketDbSchema.WordsTable.Cols.LEVEL_T +", "+
        PocketDbSchema.WordsTable.Cols.CREATE_DATE +", "+
        PocketDbSchema.WordsTable.Cols.REPEAT_STATE_W +", "+
        PocketDbSchema.WordsTable.Cols.REPEAT_STATE_P +", "+
        PocketDbSchema.WordsTable.Cols.REPEAT_STATE_T +
        ")"}  SELECT ${PocketDbSchema.WordsTable.Cols.UUID +", "+
        PocketDbSchema.WordsTable.Cols.WORD +", "+
        PocketDbSchema.WordsTable.Cols.PINYIN +", "+
        PocketDbSchema.WordsTable.Cols.TRANSLATE +", "+
        PocketDbSchema.WordsTable.Cols.COMMON_REPEAT_W +", "+
        PocketDbSchema.WordsTable.Cols.COMMON_REPEAT_P +", "+
        PocketDbSchema.WordsTable.Cols.COMMON_REPEAT_T +", "+
        PocketDbSchema.WordsTable.Cols.LEVEL_W +", "+
        PocketDbSchema.WordsTable.Cols.LEVEL_P +", "+
        PocketDbSchema.WordsTable.Cols.LEVEL_T +", "+
        PocketDbSchema.WordsTable.Cols.CREATE_DATE +", "+
        PocketDbSchema.WordsTable.Cols.REPEAT_STATE_W +", "+
        PocketDbSchema.WordsTable.Cols.REPEAT_STATE_P +", "+
        PocketDbSchema.WordsTable.Cols.REPEAT_STATE_T} FROM ${PocketDbSchema.WordsTable.NAME+"t"};"
private const val WORDS_LIST_UPGRADE_1_4 = "DROP TABLE IF EXISTS ${PocketDbSchema.WordsTable.NAME+"t"};"

private const val STUDY_LIST_UPGRADE_2 ="ALTER TABLE ${PocketDbSchema.StudyListTable.NAME} ADD COLUMN ${PocketDbSchema.StudyListTable.Cols.NOTIFY} DEFAULT 'true' "

class PocketBaseHelper(context: Context) : SQLiteOpenHelper(context, POCKET_DB_NAME, null, POCKET_DB_VERSION) {

    val database : SQLiteDatabase by lazy { getWritableDB() }


    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(STUDY_LIST_CREATE_ENTRY)
        db.execSQL(SW_LINK_CREATE_ENTRY)
        db.execSQL(WORDS_CREATE_ENTRY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (newVersion>oldVersion){

            if(oldVersion < 2){
                db?.execSQL(WORDS_LIST_UPGRADE_1_1)
                db?.execSQL(WORDS_LIST_UPGRADE_1_2)
                db?.execSQL(WORDS_LIST_UPGRADE_1_3)
                db?.execSQL(WORDS_LIST_UPGRADE_1_4)
            }
            if (oldVersion < 3){
                db?.execSQL(STUDY_LIST_UPGRADE_2)
            }
        }
    }

    override fun close() {
        if (database.isOpen){
            database.close()
        }
        super.close()
    }

    private fun getWritableDB(): SQLiteDatabase = writableDatabase

}
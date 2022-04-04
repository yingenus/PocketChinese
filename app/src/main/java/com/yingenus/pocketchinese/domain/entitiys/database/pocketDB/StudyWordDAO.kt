package com.yingenus.pocketchinese.domain.entitiys.database.pocketDB

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import java.util.*

class StudyWordDAO(db: SQLiteDatabase): PocketDAOs(db) {

    fun create(words:List<StudyWord>){
        words.forEach { create(it) }
    }
    fun create(word: StudyWord){
        database!!.insert(PocketDbSchema.WordsTable.NAME,null, getContentValues(word))
    }

    fun getAll():List<StudyWord>?{
        val cursor=queryWordsTable(null,null)
        try {
            if (cursor.count==0)
                return Collections.emptyList()
            val list= mutableListOf<StudyWord>()
            cursor.moveToFirst()
            while (!cursor.isAfterLast){
                list.add(cursor.getWord())
                cursor.moveToNext()
            }
            return list
        }finally {
            cursor.close()
        }
    }


    fun getAllInSorted(studyList: StudyList) = getAllIn(studyList)!!.groupBy({it.first}, {it.second})

    fun getAllInSorted(studyListUUID: UUID) = getAllIn(studyListUUID)!!.groupBy({it.first}, {it.second})

    fun getAllIn(studyList: StudyList)= getAllIn(studyList.uuid)

    fun getAllIn(studyListUUID: UUID):List<Pair<Int, StudyWord>>?{
        val query="SELECT *\n"+
                "FROM ${PocketDbSchema.WordsTable.NAME} \n"+
                "INNER JOIN ${PocketDbSchema.StudyWordLinkTable.NAME} ON "+
                "${PocketDbSchema.StudyWordLinkTable.NAME}.${PocketDbSchema.StudyWordLinkTable.Cols.WORDS_UUID} = "+
                "${PocketDbSchema.WordsTable.NAME}.${PocketDbSchema.WordsTable.Cols.UUID} \n"+
                "WHERE ${PocketDbSchema.StudyWordLinkTable.NAME}.${PocketDbSchema.StudyWordLinkTable.Cols.LIST_UUID} = ? \n"+
                "ORDER BY ${PocketDbSchema.StudyWordLinkTable.NAME}.${PocketDbSchema.StudyWordLinkTable.Cols.BLOCK}"

        val cursor= WordCursorWrapper(database!!.rawQuery(query, arrayOf(studyListUUID.toString())))
        try {
            if (cursor.count==0)
                return Collections.emptyList()

            val words= mutableListOf<Pair<Int, StudyWord>>()

            cursor.moveToFirst()
            while (!cursor.isAfterLast){
                words.add(cursor.getInt(cursor.getColumnIndex(PocketDbSchema.StudyWordLinkTable.Cols.BLOCK))
                        to cursor.getWord())
                cursor.moveToNext()
            }
            return words
        }finally {
            cursor.close()
        }
    }

    fun get(wordUUID: UUID): StudyWord?{
        val cursor=queryWordsTable(PocketDbSchema.WordsTable.Cols.UUID+" =?", arrayOf(wordUUID.toString()))

        try {
            if (cursor.count==0)
                return null

            cursor.moveToFirst()
            return cursor.getWord()

        }finally {
            cursor.close()
        }
    }

    fun get(word:String): StudyWord?{
        val cursor=queryWordsTable(PocketDbSchema.WordsTable.Cols.WORD, arrayOf(word))
        try {
            if (cursor.count==0)
                return null
            cursor.moveToFirst()
            return cursor.getWord()
        }finally {
            cursor.close()
        }
    }

    fun update(studyWord: StudyWord){
        val uuid=studyWord.uuid.toString()
        database!!.update(PocketDbSchema.WordsTable.NAME,getContentValues(studyWord),
                PocketDbSchema.WordsTable.Cols.UUID+" = ?", arrayOf(uuid))
    }

    fun updateAll(studyWords: List<StudyWord>){
        studyWords.forEach{
            update(it)
        }
    }

    fun remove(studyWord: StudyWord) = removeWord(studyWord.uuid)

    fun removeWord(studyWordUUID: UUID){
        val uuid=studyWordUUID.toString()
        database!!.delete(PocketDbSchema.WordsTable.NAME,
                PocketDbSchema.WordsTable.Cols.UUID+" =?", arrayOf(uuid))
    }


    private fun getContentValues(studyWord: StudyWord): ContentValues {
        val values= ContentValues()
        values.put(PocketDbSchema.WordsTable.Cols.WORD,studyWord.chinese)
        values.put(PocketDbSchema.WordsTable.Cols.PINYIN,studyWord.pinyin)
        values.put(PocketDbSchema.WordsTable.Cols.TRANSLATE,studyWord.translate)
        values.put(PocketDbSchema.WordsTable.Cols.COMMON_REPEAT_W,studyWord.chnRepeat)
        values.put(PocketDbSchema.WordsTable.Cols.COMMON_REPEAT_P,studyWord.pinRepeat)
        values.put(PocketDbSchema.WordsTable.Cols.COMMON_REPEAT_T,studyWord.trnRepeat)
        values.put(PocketDbSchema.WordsTable.Cols.LEVEL_W,studyWord.chnLevel)
        values.put(PocketDbSchema.WordsTable.Cols.LEVEL_P,studyWord.pinLevel)
        values.put(PocketDbSchema.WordsTable.Cols.LEVEL_T,studyWord.trnLevel)
        values.put(PocketDbSchema.WordsTable.Cols.LAST_REPEAT_W,studyWord.chnLastRepeat.time)
        values.put(PocketDbSchema.WordsTable.Cols.LAST_REPEAT_P,studyWord.pinLastRepeat.time)
        values.put(PocketDbSchema.WordsTable.Cols.LAST_REPEAT_T,studyWord.trnLastRepeat.time)
        values.put(PocketDbSchema.WordsTable.Cols.CREATE_DATE,studyWord.createDate.time)
        values.put(PocketDbSchema.WordsTable.Cols.UUID,studyWord.uuid.toString())
        values.put(PocketDbSchema.WordsTable.Cols.REPEAT_STATE_W,studyWord.chnRepState)
        values.put(PocketDbSchema.WordsTable.Cols.REPEAT_STATE_P,studyWord.pinRepState)
        values.put(PocketDbSchema.WordsTable.Cols.REPEAT_STATE_T,studyWord.trnRepState)

        return values
    }

    private fun queryWordsTable(wereCase:String?,wereArgs: Array<String>?)=
            WordCursorWrapper(database!!.query(PocketDbSchema.WordsTable.NAME,
                    null, wereCase, wereArgs, null, null, null))

}
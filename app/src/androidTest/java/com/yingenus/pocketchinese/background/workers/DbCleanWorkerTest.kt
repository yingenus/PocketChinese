package com.yingenus.pocketchinese.background.workers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.testing.TestWorkerBuilder
import com.yingenus.pocketchinese.domain.entitiys.database.PocketDBOpenManger
import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.*
import org.junit.*

import org.junit.Assert.*
import org.junit.runner.RunWith
import java.lang.RuntimeException
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@RunWith(AndroidJUnit4::class)
class DbCleanWorkerTest {

    private lateinit var context: Context
    private lateinit var executor: Executor
    private var unUsedWords : List<StudyWord> = emptyList()
    private var usedWords : List<StudyWord> = emptyList()
    private var unUsedConnections : List<Connection> = emptyList()
    private var usedConnections : List<Connection> = emptyList()

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        executor = Executors.newSingleThreadExecutor()
        initDB()
    }

    private fun initDB(){

        val pocketBaseHelper = PocketBaseHelper(context)
        pocketBaseHelper.database
        val db = pocketBaseHelper.database

        if (!dbIsEmpty(db, PocketDbSchema.WordsTable.NAME)
            || !dbIsEmpty(db, PocketDbSchema.StudyWordLinkTable.NAME)
            || !dbIsEmpty(db, PocketDbSchema.StudyListTable.NAME))
            throw RuntimeException("db is not empty")

        val listDAO = StudyListDAO(db)
        val connectionDAO = ConnectionDAO(db)
        val wordDAO = StudyWordDAO(db)

        val list = StudyList("test")
        val list2 = StudyList("test2")

        val goodDate = GregorianCalendar.getInstance().time
        val bedDate = GregorianCalendar(2020,2,2).time

        val word1 = StudyWord("test","test","test",pinLevel = 1,trnLevel = 2,chnLevel = 2, pinLastRepeat = goodDate, trnLastRepeat = goodDate,chnLastRepeat = goodDate)
        val word2 = StudyWord("test1","test1","test1",pinLevel = 1,trnLevel = 2,chnLevel = 2, pinLastRepeat = goodDate, trnLastRepeat = goodDate,chnLastRepeat = goodDate)
        val word3 = StudyWord("test1","test1","test1",pinLevel = 1,trnLevel = 2,chnLevel = 2, pinLastRepeat = bedDate, trnLastRepeat = goodDate,chnLastRepeat = goodDate)
        val word4 = StudyWord("test1","test1","test1",pinLevel = 1,trnLevel = 2,chnLevel = 2, pinLastRepeat = goodDate, trnLastRepeat = bedDate,chnLastRepeat = goodDate)
        val word5 = StudyWord("test1","test1","test1",pinLevel = 1,trnLevel = 2,chnLevel = 2, pinLastRepeat = goodDate, trnLastRepeat = goodDate,chnLastRepeat = bedDate)
        val word6 = StudyWord("test1","test1","test1",pinLevel = 1,trnLevel = 2,chnLevel = 2, pinLastRepeat = bedDate, trnLastRepeat = bedDate,chnLastRepeat = bedDate)
        val word7 = StudyWord("test1","test1","test1",pinLevel = 1,trnLevel = 2,chnLevel = 2, pinLastRepeat = bedDate, trnLastRepeat = bedDate,chnLastRepeat = bedDate)
        val word8 = StudyWord("test1","test1","test1",pinLevel = 1,trnLevel = 2,chnLevel = 2, pinLastRepeat = goodDate, trnLastRepeat = goodDate,chnLastRepeat = bedDate)
        val word9 = StudyWord("test1","test1","test1",pinLevel = 1,trnLevel = 2,chnLevel = 2, pinLastRepeat = goodDate, trnLastRepeat = goodDate,chnLastRepeat = goodDate)

        wordDAO.create(listOf(word1,word2,word3,word4,word5,word6,word7,word8,word9))
        listDAO.create(list)
        listDAO.create(list2)



        val unConnection1 = Connection(UUID.randomUUID(), UUID.randomUUID(),2)
        val unConnection2 = Connection(UUID.randomUUID(), word8.uuid,2)
        val connection1 = Connection(list.uuid,word1.uuid,1)
        val connection2 = Connection(list.uuid,word2.uuid,1)
        val connection3 = Connection(list2.uuid,word3.uuid,1)
        val connection4 = Connection(list2.uuid,word4.uuid,1)
        val connection5 = Connection(list2.uuid,word5.uuid,1)
        val connection6 = Connection(list2.uuid,word6.uuid,1)

        connectionDAO.create(connection1)
        connectionDAO.create(connection2)
        connectionDAO.create(connection3)
        connectionDAO.create(connection4)
        connectionDAO.create(connection5)
        connectionDAO.create(connection6)

        connectionDAO.create(unConnection1)
        connectionDAO.create(unConnection2)

        unUsedWords = listOf(word7,word8,word9)
        usedWords = listOf(word1,word2,word3,word4,word5,word6)

        unUsedConnections = listOf(unConnection1,unConnection2)
        usedConnections =  listOf(connection1,connection2,connection3,connection4,connection5,connection6)

        PocketDBOpenManger.setHelper(pocketBaseHelper)

    }

    private fun dbIsEmpty(db : SQLiteDatabase, table : String): Boolean{
        val cursor = db.rawQuery("SELECT * FROM " + table, null)
        val rowExists: Boolean = cursor.moveToFirst()
        cursor.close()
        return  !rowExists
    }

    @Ignore
    @Test
    fun testCheker(){
        val worked = TestWorkerBuilder<DbCleanWorker>(context,executor).build()

        val result = worked.doWork()

        assertTrue(result is ListenableWorker.Result.Success)

        val db = PocketDBOpenManger.getHelper(context).writableDatabase

        val connectionDAO = ConnectionDAO(db)
        val wordDAO = StudyWordDAO(db)

        val words = wordDAO.getAll()
        val connections = connectionDAO.getAll()

        connectionDAO.finish()
        wordDAO.finish()
        PocketDBOpenManger.releaseHelper()


        usedWords.forEach {
            assertTrue(words!!.contains(it))
        }
        usedConnections.forEach {
            assertTrue(connections!!.contains(it))
        }
        unUsedWords.forEach { word ->
            assertFalse(words!!.contains(word))
        }
        unUsedConnections.forEach { connection ->
            assertFalse(connections!!.contains(connection))
        }

    }

    @After
    fun tearDown() {
        val db = PocketDBOpenManger.getHelper(context).writableDatabase
        db.execSQL("DROP TABLE IF EXISTS ${PocketDbSchema.StudyListTable.NAME};")
        db.execSQL("DROP TABLE IF EXISTS ${PocketDbSchema.StudyWordLinkTable.NAME};")
        db.execSQL("DROP TABLE IF EXISTS ${PocketDbSchema.StudyWordLinkTable.NAME};")
        db.close()
        PocketDBOpenManger.releaseHelper()
        PocketDBOpenManger.releaseHelper()
    }
}
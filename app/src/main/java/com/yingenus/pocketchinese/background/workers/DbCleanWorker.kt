package com.yingenus.pocketchinese.background.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.yingenus.pocketchinese.PocketApplication
import com.yingenus.pocketchinese.logErrorMes
import com.yingenus.pocketchinese.domain.entitiys.database.PocketDBOpenManger
import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.*
import java.sql.SQLException

class DbCleanWorker(context : Context, workerParams : WorkerParameters ) : Worker(context, workerParams){

    override fun doWork(): Result {
        Log.d("dbCleaner","Worker start")
        PocketApplication.setupApplication()
        return try{
            val cleaner = Cleaner(applicationContext)

            cleaner.clean()
            cleaner.finish()

            Result.success()

        }catch (e: SQLException){
            Log.e("dbCleaner", e.cause?.logErrorMes()?:"error with no msg")
            Result.failure()
        }
    }

    private class Cleaner( context : Context){

        private val listDAO : StudyListDAO
        private val connectionDAO : ConnectionDAO
        private val wordDAO : StudyWordDAO

        init {
            val sqlDb = PocketDBOpenManger.getHelper(context).writableDatabase

            listDAO = StudyListDAO(sqlDb)
            connectionDAO = ConnectionDAO(sqlDb)
            wordDAO = StudyWordDAO(sqlDb)
        }


        fun clean(){

            val lists = listDAO.getAll()

            val connections = lists?.flatMap { connectionDAO.getAll(it.uuid)?: emptyList() }

            val words = connections?.map { wordDAO.get( it.wordUUID) }

            val dbAllWords = wordDAO.getAll()
            val dbAllConnections = connectionDAO.getAll()

            val cleanWordsCount: Int = cleanWords(dbAllWords,words)

            val cleanConnectionsCount : Int = cleanConnections(dbAllConnections,connections)

            Log.i("dbCleaner", "cleaned: $cleanWordsCount Words and $cleanConnectionsCount Connections")
        }

        private fun cleanWords(allWords : List<StudyWord?>?, liquidWords : List<StudyWord?>?): Int{
            var removedCount = 0

            if (liquidWords.isNullOrEmpty()){

                allWords?.forEach { word : StudyWord? ->
                    if (word != null){
                        wordDAO.remove(word)
                        removedCount += 1
                    }
                }
            }else{
                allWords?.forEach { word: StudyWord? ->
                    if (word != null && !liquidWords.contains(word)){
                        wordDAO.remove(word)
                        removedCount += 1
                    }
                }
            }
            return removedCount
        }

        private fun cleanConnections(allConnections : List<Connection?>?, liquidConnections : List<Connection?>?): Int{
            var removedCount = 0

            if (liquidConnections.isNullOrEmpty()){

                allConnections?.forEach { connection : Connection? ->
                    if (connection != null){
                        connectionDAO.remove(connection)
                        removedCount += 1
                    }
                }
            }else{
                allConnections?.forEach { connection: Connection? ->
                    if (connection != null && !liquidConnections.contains(connection)){
                        connectionDAO.remove(connection)
                        removedCount += 1
                    }
                }
            }
            return removedCount
        }

        fun finish(){
            wordDAO.finish()
            connectionDAO.finish()
            listDAO.finish()
            PocketDBOpenManger.releaseHelper()
        }

    }
}
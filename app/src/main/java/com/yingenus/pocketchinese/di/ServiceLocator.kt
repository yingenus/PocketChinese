package com.yingenus.pocketchinese.di

import android.content.Context
import androidx.room.Room
import com.yingenus.pocketchinese.data.local.RoomExampleRepository
import com.yingenus.pocketchinese.data.local.RoomWordRepository
import com.yingenus.pocketchinese.data.local.room.ExamplesDb
import com.yingenus.pocketchinese.data.local.room.WordsDb
import com.yingenus.pocketchinese.domain.repository.ChinCharRepository
import com.yingenus.pocketchinese.domain.repository.ExampleRepository
import com.yingenus.pocketchinese.domain.repository.RadicalsRepository
import com.yingenus.pocketchinese.domain.repository.ToneRepository
import java.lang.IllegalArgumentException


object ServiceLocator {
    @Volatile
    private var examplesDb : ExamplesDb? = null
    @Volatile
    private var wordsDb : WordsDb? = null
    @Volatile
    private var roomExampleRepository : RoomExampleRepository? = null
    @Volatile
    private var roomWordRepository : RoomWordRepository? = null


    fun <T> get( context : Context, className : String): T{
        return when(className){
            ExamplesDb::class.java.name ->{
                if (examplesDb == null){
                    val db = Room.databaseBuilder(context,ExamplesDb::class.java,"exampleDB.db")
                            .createFromAsset("exampleDB.db")
                            .fallbackToDestructiveMigration()
                            .enableMultiInstanceInvalidation()
                            .allowMainThreadQueries().build()
                    examplesDb = db
                }
                examplesDb!! as T
            }
            WordsDb::class.java.name ->{
                if (wordsDb == null){
                    val db = Room.databaseBuilder(context,WordsDb::class.java,"dictionaryDB.db")
                            .createFromAsset("dictionaryDB.db")
                            .fallbackToDestructiveMigration()
                            .enableMultiInstanceInvalidation()
                            .allowMainThreadQueries().build()
                    wordsDb = db
                }
                wordsDb!! as T
            }
            RoomExampleRepository::class.java.name ->{
                if (roomExampleRepository == null)
                    roomExampleRepository = RoomExampleRepository(get(context,ExamplesDb::class.java.name))
                roomExampleRepository as T
            }
            RoomWordRepository::class.java.name ->{
                if (roomWordRepository == null)
                    roomWordRepository = RoomWordRepository(get(context,WordsDb::class.java.name))
                roomWordRepository as T
            }
            ChinCharRepository::class.java.name -> get(context,RoomWordRepository::class.java.name) as T
            RadicalsRepository::class.java.name -> get(context,RoomWordRepository::class.java.name) as T
            ToneRepository::class.java.name -> get(context,RoomWordRepository::class.java.name) as T
            ExampleRepository::class.java.name -> get(context,RoomExampleRepository::class.java.name) as T
            else -> throw IllegalArgumentException("cant provide class :${className}")
        }
    }
}
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


    fun <T> get( context : Context, clazz: Class<T>): T{
        return when(clazz){
            ExamplesDb::class ->{
                if (examplesDb == null){
                    val db = Room.databaseBuilder(context,ExamplesDb::class.java,"examplesDB.db")
                            .createFromAsset("/").allowMainThreadQueries().build()
                    examplesDb = db
                }
                examplesDb!! as T
            }
            WordsDb::class ->{
                if (examplesDb == null){
                    val db = Room.databaseBuilder(context,WordsDb::class.java,"examplesDB.db")
                            .createFromAsset("/").allowMainThreadQueries().build()
                    wordsDb = db
                }
                wordsDb!! as T
            }
            RoomExampleRepository::class ->{
                if (roomExampleRepository == null)
                    roomExampleRepository = RoomExampleRepository(get(context,ExamplesDb::class.java))
                roomExampleRepository as T
            }
            RoomWordRepository::class ->{
                if (roomWordRepository == null)
                    roomWordRepository = RoomWordRepository(get(context,WordsDb::class.java))
                roomWordRepository as T
            }
            ChinCharRepository::class -> get(context,RoomWordRepository::class.java) as T
            RadicalsRepository::class -> get(context,RoomWordRepository::class.java) as T
            ToneRepository::class -> get(context,RoomWordRepository::class.java) as T
            ExampleRepository::class -> get(context,RoomExampleRepository::class.java) as T
            else -> throw IllegalArgumentException("cant provide class :${clazz.name}")
        }
    }
}
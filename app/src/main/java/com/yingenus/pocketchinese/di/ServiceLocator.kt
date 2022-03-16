package com.yingenus.pocketchinese.di

import android.content.Context
import androidx.room.Room
import com.yingenus.pocketchinese.data.local.*
import com.yingenus.pocketchinese.data.local.db.DatabaseManager
import com.yingenus.pocketchinese.data.local.db.InAssetsDatabaseManager

import com.yingenus.pocketchinese.data.local.room.ExamplesDb
import com.yingenus.pocketchinese.data.local.room.WordsDb
import com.yingenus.pocketchinese.domain.entities.dictionarysearch.FuzzySearchEngine
import com.yingenus.pocketchinese.domain.entities.dictionarysearch.MatchSearchEngine
import com.yingenus.pocketchinese.domain.entities.dictionarysearch.SearchEngine
import com.yingenus.pocketchinese.domain.repository.DictionaryItemRepository
import com.yingenus.pocketchinese.domain.repository.ExampleRepository
import com.yingenus.pocketchinese.domain.repository.RadicalsRepository
import com.yingenus.pocketchinese.domain.repository.ToneRepository
import com.yingenus.pocketchinese.domain.repository.search.NgramM3Repository
import com.yingenus.pocketchinese.domain.usecase.WordSearchUseCaseImpl
import com.yingenus.pocketchinese.domain.usecase.WordsSearchUseCase
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
    @Volatile
    private var wordsSearchUseCase : WordsSearchUseCase? = null
    @Volatile
    private var matchSearchEngine : SearchEngine? = null
    @Volatile
    private var fuzzySearchEngine : SearchEngine? = null
    @Volatile
    private var roomPinSearchRepository : RoomPinSearchRepository? = null
    @Volatile
    private var roomRusSearchRepository : RoomRusSearchRepository? = null
    @Volatile
    private var roomChnN1SearchRepository : RoomChnN1SearchRepository? = null
    @Volatile
    private var roomChnN2SearchRepository : RoomChnN2SearchRepository? = null
    @Volatile
    private var databaseManager : DatabaseManager? = null

    fun <T> get( context : Context, className : String): T{
        return when(className){

            DatabaseManager::class.java.name ->{
                if(databaseManager == null){
                    databaseManager = InAssetsDatabaseManager()
                }
                return databaseManager!! as T
            }
            ExamplesDb::class.java.name ->{
                /*
                if (examplesDb == null){
                    val db = Room.databaseBuilder(context,ExamplesDb::class.java,"exampleDB.db")
                            .createFromAsset("exampleDB.db")
                            .fallbackToDestructiveMigration()
                            .enableMultiInstanceInvalidation()
                            .allowMainThreadQueries().build()
                    examplesDb = db
                }
                examplesDb!! as T

                 */
                return (get(context,DatabaseManager::class.java.name) as DatabaseManager)
                    .getExampleDatabase(context) as T
            }
            WordsDb::class.java.name ->{
                /*
                if (wordsDb == null){
                    val db = Room.databaseBuilder(context,WordsDb::class.java,"dictionaryDB.db")
                            .createFromAsset("dictionaryDB.db")
                            .fallbackToDestructiveMigration()
                            .enableMultiInstanceInvalidation()
                            .allowMainThreadQueries().build()
                    wordsDb = db
                }
                wordsDb!! as T
                 */
                return (get(context,DatabaseManager::class.java.name) as DatabaseManager)
                    .getWordsDatabase( context) as T
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
            RoomRusSearchRepository::class.java.name ->{
                if (roomRusSearchRepository == null){
                    roomRusSearchRepository = RoomRusSearchRepository( get(context,WordsDb::class.java.name) )
                }
                roomRusSearchRepository as T
            }
            RoomPinSearchRepository::class.java.name ->{
                if (roomPinSearchRepository == null){
                    roomPinSearchRepository = RoomPinSearchRepository( get(context,WordsDb::class.java.name) )
                }
                roomPinSearchRepository as T
            }
            RoomChnN2SearchRepository::class.java.name ->{
                if (roomChnN2SearchRepository == null){
                    roomChnN2SearchRepository = RoomChnN2SearchRepository(get(context,WordsDb::class.java.name))
                }
                roomChnN2SearchRepository as T
            }
            RoomChnN1SearchRepository::class.java.name ->{
                if (roomChnN1SearchRepository == null){
                    roomChnN1SearchRepository = RoomChnN1SearchRepository(get(context,WordsDb::class.java.name))
                }
                roomChnN1SearchRepository as T
            }
            MatchSearchEngine::class.java.name -> {
                if (matchSearchEngine == null){
                    matchSearchEngine = MatchSearchEngine( get(context,DictionaryItemRepository::class.java.name) as DictionaryItemRepository )
                }
                matchSearchEngine as T
            }
            FuzzySearchEngine::class.java.name -> {
                if (fuzzySearchEngine == null){
                    fuzzySearchEngine = FuzzySearchEngine(
                            dictionaryItemRepository = get(context,DictionaryItemRepository::class.java.name) as DictionaryItemRepository,
                            rusNgramRepository = getRusNgramRepository(context),
                            pinNgramRepository = getPinNgramRepository(context),
                            chnN1gramRepository = get(context,RoomChnN1SearchRepository::class.java.name),
                            chnN2gramRepository = get(context,RoomChnN2SearchRepository::class.java.name),
                            rusUnitWordRepository = BruteUnitWordsRepository( get(context,RoomRusSearchRepository::class.java.name)),
                            pinUnitWordRepository = get(context,RoomPinSearchRepository::class.java.name)
                    )
                }
                fuzzySearchEngine as T
            }
            DictionaryItemRepository::class.java.name -> get(context,RoomWordRepository::class.java.name) as T
            RadicalsRepository::class.java.name -> get(context,RoomWordRepository::class.java.name) as T
            ToneRepository::class.java.name -> get(context,RoomWordRepository::class.java.name) as T
            ExampleRepository::class.java.name -> get(context,RoomExampleRepository::class.java.name) as T
            WordsSearchUseCase::class.java.name -> {
                if (wordsSearchUseCase == null){
                    wordsSearchUseCase = WordSearchUseCaseImpl(
                            matchSearch = get(context,MatchSearchEngine::class.java.name) as SearchEngine,
                            fuzzySearch = get(context, FuzzySearchEngine::class.java.name) as SearchEngine
                    )
                }
                wordsSearchUseCase as T
            }
            else -> throw IllegalArgumentException("cant provide class :${className}")
        }
    }

    private fun getRusNgramRepository(context: Context): NgramM3Repository<Int>{
        return get(context,RoomRusSearchRepository::class.java.name)
    }

    private fun getPinNgramRepository(context: Context): NgramM3Repository<Int>{
        return get(context, RoomPinSearchRepository::class.java.name)
    }
}
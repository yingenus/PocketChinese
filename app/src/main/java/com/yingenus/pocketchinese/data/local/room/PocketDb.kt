package com.yingenus.pocketchinese.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yingenus.pocketchinese.data.local.room.entity.Repeat
import com.yingenus.pocketchinese.data.local.room.entity.Statistic
import com.yingenus.pocketchinese.data.local.room.entity.StudyList
import com.yingenus.pocketchinese.data.local.room.entity.StudyWord

@Database(entities = [Repeat::class,Statistic::class,StudyList::class,StudyWord::class], version = 4)
abstract class PocketDb : RoomDatabase() {
    abstract fun repeatDao() : RepeatDao
    abstract fun statisticDao() : StatisticDao
    abstract fun studylistDao() : StudyListDao
    abstract fun studywordDao() : StudyWordDao

    companion object{
        val migration_3_4 = object : Migration(3,4){
            override fun migrate(database: SupportSQLiteDatabase) {
                //to do nothing yet
            }
        }
    }

}


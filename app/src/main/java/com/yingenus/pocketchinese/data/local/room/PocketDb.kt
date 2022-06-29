package com.yingenus.pocketchinese.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yingenus.pocketchinese.data.local.room.entity.Repeat
import com.yingenus.pocketchinese.data.local.room.entity.Statistic
import com.yingenus.pocketchinese.data.local.room.entity.StudyList
import com.yingenus.pocketchinese.data.local.room.entity.StudyWord

@Database(entities = [Repeat::class,Statistic::class,StudyList::class,StudyWord::class], version = 4)
@TypeConverters(DateConverters::class)
abstract class PocketDb : RoomDatabase() {
    abstract fun repeatDao() : RepeatDao
    abstract fun statisticDao() : StatisticDao
    abstract fun studylistDao() : StudyListDao
    abstract fun studywordDao() : StudyWordDao

    companion object{
        val migration_3_4 = object : Migration(3,4){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE words RENAME TO words_tmp;")
                database.execSQL("CREATE TABLE `study_words` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `wordchn` TEXT NOT NULL, `pinyin` TEXT NOT NULL, `translate` TEXT NOT NULL, `study_list` INTEGER NOT NULL, `create_date` INTEGER NOT NULL,uuid);")
                database.execSQL("ALTER TABLE study_list RENAME TO study_list_tmp;")
                database.execSQL("CREATE TABLE `study_list` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `create_date` INTEGER NOT NULL, `notify` INTEGER NOT NULL, uuid);")
                database.execSQL("INSERT INTO study_list ( name, create_date, notify, uuid) SELECT study_list_tmp.name, study_list_tmp.update_date, study_list_tmp.notify, study_list_tmp.uuid FROM study_list_tmp;")
                database.execSQL("INSERT INTO study_words (wordchn, pinyin, translate, study_list, create_date, uuid) SELECT words_tmp.wordchn, words_tmp.pinyin, words_tmp.translate, study_list.id, words_tmp.create_date, words_tmp.uuid  FROM  words_tmp INNER JOIN sw_linc ON sw_linc.word = words_tmp.uuid INNER JOIN study_list ON study_list.uuid = sw_linc.list;")
                database.execSQL("CREATE TABLE `repeat` (`word_id` INTEGER NOT NULL, `chin_lvl` INTEGER NOT NULL, `pin_lvl` INTEGER NOT NULL, `trn_lvl` INTEGER NOT NULL, `train_status_chn` INTEGER NOT NULL, `train_count_chn` INTEGER NOT NULL, `train_status_pin` INTEGER NOT NULL, `train_count_pin` INTEGER NOT NULL, `train_status_trn` INTEGER NOT NULL, `train_count_trn` INTEGER NOT NULL, `train_date_chn` INTEGER NOT NULL, `train_date_pin` INTEGER NOT NULL, `train_date_trn` INTEGER NOT NULL, PRIMARY KEY(`word_id`));")
                database.execSQL("INSERT INTO repeat (word_id,chin_lvl,pin_lvl,trn_lvl,train_count_chn,train_count_pin,train_count_trn,train_date_chn,train_date_pin,train_date_trn, train_status_chn,train_status_pin,train_status_trn)  SELECT study_words.id, words_tmp.levelw, words_tmp.levelp, words_tmp.levelt, words_tmp.repeatw, words_tmp.repeatp, words_tmp.repeatt, COALESCE(words_tmp.lastw, 0), COALESCE(words_tmp.lastp, 0), COALESCE(words_tmp.lastt, 0), 0, 0, 0 FROM words_tmp INNER JOIN study_words ON study_words.uuid = words_tmp.uuid;")
                database.execSQL("DROP TABLE words_tmp;")
                database.execSQL("DROP TABLE study_list_tmp;")
                database.execSQL("DROP TABLE sw_linc;")
                database.execSQL("ALTER TABLE study_words RENAME TO study_words_tmp;")
                database.execSQL("CREATE TABLE `study_words` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `wordchn` TEXT NOT NULL, `pinyin` TEXT NOT NULL, `translate` TEXT NOT NULL, `study_list` INTEGER NOT NULL, `create_date` INTEGER NOT NULL);")
                database.execSQL("INSERT INTO study_words SELECT id,wordchn,pinyin,translate,study_list,create_date FROM study_words_tmp;")
                database.execSQL("DROP TABLE study_words_tmp;")
                database.execSQL("ALTER TABLE study_list RENAME TO study_list_tmp;")
                database.execSQL("CREATE TABLE `study_list` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `create_date` INTEGER NOT NULL, `notify` INTEGER NOT NULL);")
                database.execSQL("INSERT INTO study_list SELECT id,name,create_date,notify FROM study_list_tmp;")
                database.execSQL("DROP TABLE study_list_tmp;")
                database.execSQL("CREATE TABLE `user_statistic` (`date` INTEGER NOT NULL, `added` TEXT NOT NULL, `deleted` TEXT NOT NULL, `repeated` TEXT NOT NULL, `chn_passed` INTEGER NOT NULL, `chn_failed` INTEGER NOT NULL, `pin_passed` INTEGER NOT NULL, `pin_failed` INTEGER NOT NULL, `trn_passed` INTEGER NOT NULL, `trn_failed` INTEGER NOT NULL, PRIMARY KEY(`date`));")
            }
        }
    }

}


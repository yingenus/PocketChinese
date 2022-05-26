package com.yingenus.pocketchinese.data.local

import com.yingenus.pocketchinese.data.local.room.*
import com.yingenus.pocketchinese.data.local.room.entity.Repeat
import com.yingenus.pocketchinese.data.local.room.entity.Statistic
import com.yingenus.pocketchinese.data.local.room.entity.StudyListUpdate
import com.yingenus.pocketchinese.data.local.room.entity.StudyWordUpdate
import com.yingenus.pocketchinese.domain.dto.*
import com.yingenus.pocketchinese.domain.repository.StudyRepository
import com.yingenus.pocketchinese.domain.repository.TrainingRepository
import com.yingenus.pocketchinese.data.local.room.entity.StudyList as RoomStudyList
import com.yingenus.pocketchinese.data.local.room.entity.StudyWord as RoomStudyWord
import com.yingenus.pocketchinese.domain.repository.UserStatisticRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class RoomPocketRepository @Inject constructor(private val pocketDb: PocketDb) :
    UserStatisticRepository, StudyRepository, TrainingRepository{

    private val studyWordDao : StudyWordDao = pocketDb.studywordDao()
    private val studyListDao : StudyListDao = pocketDb.studylistDao()
    private val repeatDao : RepeatDao = pocketDb.repeatDao()
    private val statisticDao : StatisticDao = pocketDb.statisticDao()

    override fun getAllStudyList(): Single<List<StudyList>> {
        return Single.defer{
            Single.create<List<StudyList>> { it.onSuccess(
                studyListDao.getAll()
                    .map { it.toStudyList() }) }}
            .subscribeOn(Schedulers.io())
    }

    override fun getStudyListById(id: Long): Maybe<StudyList> {
        return Maybe.defer{
            Maybe.create<StudyList> {
                val result = studyListDao.getById(id)
                if (result == null) it.onComplete()
                else it.onSuccess(result.toStudyList())

            }
                .subscribeOn(Schedulers.io())
        }
    }

    override fun getStudyListByName(name: String): Maybe<StudyList> {
        return Maybe.defer{
            Maybe.create<StudyList> {
                val result = studyListDao.getByName(name)
                if (result != null) it.onSuccess(result.toStudyList())
                else it.onComplete()
            }
                .subscribeOn(Schedulers.io())
        }
    }

    override fun createStudyList(studyList: StudyList): Completable {
        return Completable.defer {
                Completable.create {
                    studyListDao.creteStudyList(RoomStudyList.fromStudyList(studyList))
                    it.onComplete()
                }
            }.subscribeOn(Schedulers.io())
    }

    override fun updateStudyList(studyList: StudyList): Completable {
        return Completable.defer {
            Completable.create {
                studyListDao.updateStudyList(StudyListUpdate.fromStudyList(studyList))
                it.onComplete()
            }
        }.subscribeOn(Schedulers.io())
    }

    override fun deleteStudyList(studyList: StudyList): Completable {
        return Completable.defer {
            Completable.create {
                val words = studyWordDao.getStudyWords(studyList.id)
                studyListDao.deleteStudyListAndWords(RoomStudyList.fromStudyList(studyList), words)
                repeatDao.deleteByWordIds(words.map { it.id })
                it.onComplete()
            }
        }.subscribeOn(Schedulers.io())
    }

    override fun getStudyWordsByListId(id: Long): Single<List<StudyWord>> {
        return Single.defer{
            Single.create<List<StudyWord>> { it.onSuccess(
                studyWordDao.getStudyWords(id).map { it.toStudyWord() }
            )}}
            .subscribeOn(Schedulers.io())
    }

    override fun countWordsForList(id: Long): Single<Int> {
        return Single.defer{
            Single.create<Int> { it.onSuccess(
                studyWordDao.wordsInStudyList(id)
            )}}
            .subscribeOn(Schedulers.io())
    }

    override fun getStudyWordsByListName(name: String): Single<List<StudyWord>> {
        return Single.defer{
            Single.create<List<StudyWord>> {
                val studyList = studyListDao.getByName(name)
                val result =  if (studyList != null){
                    studyWordDao.getStudyWords(studyList.id).map { it.toStudyWord()}
                }else{
                    emptyList()
                }
                it.onSuccess(result)
            }}
            .subscribeOn(Schedulers.io())
    }

    override fun getStudyWord(id: Long): Maybe<StudyWord> {
        return Maybe.defer{
            Maybe.create<StudyWord> {
                val result = studyWordDao.getStudyWord(id)
                if (result != null) it.onSuccess(result.toStudyWord())
                else it.onComplete()
            }
                .subscribeOn(Schedulers.io())
        }
    }

    override fun addStudyWord(studyList: StudyList, studyWord: StudyWord): Completable {
        return Completable.defer {
            Completable.create {
                studyWordDao.creteStudyWord( RoomStudyWord.fromStudyWord(studyList.id,studyWord))
                it.onComplete()
            }
        }.subscribeOn(Schedulers.io())
    }

    override fun addStudyWordWithID(studyList: StudyList, studyWord: StudyWord): Single<Long> {
        return Single.defer<Long> {
            Single.create {
                val id = studyWordDao.creteStudyWordWithID( RoomStudyWord.fromStudyWord(studyList.id,studyWord))
                it.onSuccess(id)
            }
        }.subscribeOn(Schedulers.io())
    }

    override fun updateStudyWord(studyWord: StudyWord): Completable {
        return Completable.defer {
            Completable.create {
                studyWordDao.updateStudyWord(StudyWordUpdate.fromStudyList(studyWord))
                it.onComplete()
            }
        }.subscribeOn(Schedulers.io())
    }

    override fun deleteStudyWord(studyWord: StudyWord): Completable {
        return Completable.defer {
            Completable.create {
                studyWordDao.deleteStudyWord(StudyWordUpdate.fromStudyList(studyWord))
                it.onComplete()
            }
        }.subscribeOn(Schedulers.io())
    }

    override fun addStudyWords(studyList: StudyList ,studyWords: List<StudyWord>): Completable {
        return Completable.defer {
            Completable.create {
                val words = studyWords.map { RoomStudyWord.fromStudyWord(studyList.id,it) }
                studyWordDao.creteStudyWords(
                    words
                )
                it.onComplete()
            }
        }.subscribeOn(Schedulers.io())
    }

    override fun addStudyWordsWithID(studyList: StudyList ,studyWords: List<StudyWord>): Single<List<Long>> {
        return Single.defer<List<Long>> {
            Single.create {
                val words = studyWords.map { RoomStudyWord.fromStudyWord(studyList.id,it) }
                val ids = studyWordDao.creteStudyWordsWithID(words)
                it.onSuccess(ids)
            }
        }.subscribeOn(Schedulers.io())
    }

    override fun deleteStudyWords(studyWords: List<StudyWord>): Completable {
        return Completable.defer {
            Completable.create {
                studyWordDao.deleteStudyWordsU(
                    studyWords.map { StudyWordUpdate.fromStudyList(it) }
                )
            }
        }.subscribeOn(Schedulers.io())
    }

    override fun deleteStudyWordsByIds(ids: List<Long>): Completable {
        return Completable.defer {
            Completable.create {
                studyWordDao.deleteStudyWordsById(ids)
            }
        }.subscribeOn(Schedulers.io())
    }

    override fun getStatistic(date: Date): Maybe<UserStatistic> {
        return Maybe.defer{
            Maybe.create<UserStatistic> {
                val result = statisticDao.getStatistic(dateToDays(date))?.toUserStatistic()
                if (result != null) it.onSuccess(result)
                else it.onComplete()
            }
                .subscribeOn(Schedulers.io())
        }
    }

    override fun getStatistics(from: Date, to: Date): Single<List<UserStatistic>> {
        return Single.defer{
            Single.create<List<UserStatistic>> {
                it.onSuccess( statisticDao.getStatistic(dateToDays(from),dateToDays(to)).map { it.toUserStatistic() } )
            }
        }
            .subscribeOn(Schedulers.io())
    }

    override fun getStatisticsLast(timePeriod: TimePeriod): Single<List<UserStatistic>> {
        return Single.defer{
            Single.create<List<UserStatistic>> {
                val date = dateToDays(Date(System.currentTimeMillis()))
                it.onSuccess( statisticDao.getStatistic(dateToDays(timePeriod.beforeDate(date)),date).map { it.toUserStatistic() } )
            }
        }
            .subscribeOn(Schedulers.io())
    }

    override fun updateStatistic(userStatistic: UserStatistic): Completable {
        return Completable.defer {
            Completable.create {
                statisticDao.updateStatistic(Statistic.fromUserStatistic(userStatistic))
                it.onComplete()
            }
        }.subscribeOn(Schedulers.io())
    }

    override fun createStatistic(userStatistic: UserStatistic): Completable {
        return Completable.defer {
            Completable.create {
                statisticDao.creteStatistic(Statistic.fromUserStatistic(userStatistic))
                it.onComplete()
            }
        }.subscribeOn(Schedulers.io())
    }

    override fun getTrainingCond(studyWordId: Long): Maybe<TrainingCond> {
        return Maybe.defer{
            Maybe.create<TrainingCond> {
                val result = repeatDao.getRepeat(studyWordId)?.toTrainingCond()
                if (result != null) it.onSuccess(result)
                else it.onComplete()
            }
                .subscribeOn(Schedulers.io())
        }
    }

    override fun getTrainingCondForList(studyListId: Long): Single<List<TrainingCond>> {
        return Single.defer{
            Single.create<List<TrainingCond>> {
                val result = repeatDao.getRepeatsForList(studyListId).map { it.toTrainingCond() }
                it.onSuccess(result)
            }
        }.subscribeOn(Schedulers.io())
    }

    override fun creteTrainingCond(trainingCond: TrainingCond): Completable {
        return Completable.defer {
            Completable.create {
                repeatDao.creteRepeat(Repeat.fromTrainingCond(trainingCond))
                it.onComplete()
            }
        }.subscribeOn(Schedulers.io())
    }

    override fun deleteTrainingCond(trainingCond: TrainingCond): Completable {
        return Completable.defer {
            Completable.create {
                repeatDao.deleteRepeat(Repeat.fromTrainingCond(trainingCond))
                it.onComplete()
            }
        }.subscribeOn(Schedulers.io())
    }

    override fun deleteTrainingCondForWord( studyWordId: Long) : Completable{
        return Completable.defer {
            Completable.create {
                repeatDao.deleteByWordId(studyWordId)
                it.onComplete()
            }
        }.subscribeOn(Schedulers.io())
    }
    override fun deleteTrainingCondForList( studyListId: Long) : Completable{
        return Completable.defer {
            Completable.create {
                repeatDao.deleteRepeatByStudyListId(studyListId)
                it.onComplete()
            }
        }.subscribeOn(Schedulers.io())
    }

    override fun updateTrainingCond(trainingCond: TrainingCond): Completable {
        return Completable.defer {
            Completable.create {
                repeatDao.updateRepeat(Repeat.fromTrainingCond(trainingCond))
                it.onComplete()
            }
        }.subscribeOn(Schedulers.io())
    }

    private fun dateToDays(date: Date) : Date {
        val calendar = GregorianCalendar()
        calendar.gregorianChange = date
        val dateCalendar = GregorianCalendar()
        dateCalendar.set(
            calendar.get(GregorianCalendar.YEAR),
            calendar.get(GregorianCalendar.MONTH),
            calendar.get(GregorianCalendar.DAY_OF_MONTH)
        )
        return dateCalendar.gregorianChange
    }

}
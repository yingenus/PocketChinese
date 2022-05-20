package com.yingenus.pocketchinese.domain.entities.studystatictic

import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.domain.dto.*
import com.yingenus.pocketchinese.domain.repository.UserStatisticRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class UserStatisticImpl @Inject constructor(
    private val userStatisticRepository: UserStatisticRepository) :
    UserStatistics {


    override fun getStatistic(interval: TimePeriod): Single<ShowedUserStatistic> {
        return userStatisticRepository.getStatisticsLast(interval)
            .observeOn(Schedulers.computation())
            .map {
                if(it.isNullOrEmpty() )ShowedUserStatistic(
                    added = 0,
                    repeated = 0,
                    passedChn = 0,
                    passedPin = 0,
                    passedTrn = 0,
                    failedChn = 0,
                    failedPin = 0,
                    failedTrn = 0,
                )
                else ShowedUserStatistic(
                    added = getClearAdded(it),
                    repeated = it.map { it.repeated.size }.reduce { acc, i -> acc + i },
                    passedChn = it.map { it.passedChn }.reduce { acc, i -> acc + i },
                    passedPin = it.map { it.passedPin }.reduce { acc, i -> acc + i },
                    passedTrn = it.map { it.passedTrn }.reduce { acc, i -> acc + i },
                    failedChn = it.map { it.failedChn }.reduce { acc, i -> acc + i },
                    failedPin = it.map { it.failedPin }.reduce { acc, i -> acc + i },
                    failedTrn = it.map { it.failedTrn }.reduce { acc, i -> acc + i },
                )
            }
    }

    override fun wordAdded(id: Long): Completable {
        return userStatisticRepository
            .getStatistic(Date(System.currentTimeMillis()))
            .switchIfEmpty(creteStatistic())
            .doOnSuccess {
                it.added = it.added.toMutableList().also { it.add(id) }
                userStatisticRepository.updateStatistic(it).blockingSubscribe()
            }
            .observeOn(Schedulers.computation())
            .ignoreElement()
    }

    override fun wordsAdded(ids: List<Long>): Completable {
        return userStatisticRepository
            .getStatistic(Date(System.currentTimeMillis()))
            .switchIfEmpty(creteStatistic())
            .doOnSuccess {
                it.added = it.added.toMutableList().also { it.addAll(ids) }
                userStatisticRepository.updateStatistic(it).blockingSubscribe()
            }
            .observeOn(Schedulers.computation())
            .ignoreElement()
    }


    override fun wordDeleted(id: Long): Completable  {
        return userStatisticRepository
            .getStatistic(Date(System.currentTimeMillis()))
            .switchIfEmpty(creteStatistic())
            .doOnSuccess {
                it.deleted = it.deleted.toMutableList().also { it.add(id) }
                userStatisticRepository.updateStatistic(it).blockingSubscribe()
            }
            .observeOn(Schedulers.computation())
            .ignoreElement()
    }

    override fun wordsDeleted(ids: List<Long>): Completable  {
        return userStatisticRepository
            .getStatistic(Date(System.currentTimeMillis()))
            .switchIfEmpty(creteStatistic())
            .doOnSuccess {
                it.deleted = it.deleted.toMutableList().also { it.addAll(ids) }
                userStatisticRepository.updateStatistic(it).blockingSubscribe()
            }
            .observeOn(Schedulers.computation())
            .ignoreElement()
    }

    override fun wordTrained(id: Long, trainedResult: TrainedResult, language: Language): Completable  {
        return userStatisticRepository
            .getStatistic(Date(System.currentTimeMillis()))
            .switchIfEmpty(creteStatistic())
            .doOnSuccess {
                it.repeated = it.repeated.toMutableList().also { it.add(id) }
                when(language){
                    Language.CHINESE ->{
                        if (trainedResult == TrainedResult.SUCCESS || trainedResult == TrainedResult.SUCCESS_AFTER_FILED)
                            it.passedChn ++ else it.failedChn ++
                    }
                    Language.PINYIN ->{
                        if (trainedResult == TrainedResult.SUCCESS || trainedResult == TrainedResult.SUCCESS_AFTER_FILED)
                            it.passedPin ++ else it.failedPin ++
                    }
                    Language.RUSSIAN ->{
                        if (trainedResult == TrainedResult.SUCCESS || trainedResult == TrainedResult.SUCCESS_AFTER_FILED)
                            it.passedTrn ++ else it.failedTrn ++
                    }
                }
                userStatisticRepository.updateStatistic(it).blockingSubscribe()
            }
            .observeOn(Schedulers.computation())
            .ignoreElement()
    }

    private fun creteStatistic(): Single<UserStatistic>{
        return Single.defer { Single.create<UserStatistic> {
            it.onSuccess(UserStatistic(
                dateToDays(Date(System.currentTimeMillis())),
                emptyList(),
                emptyList(),
                emptyList(),
                0,
                0,
                0,
                0,
                0,
                0
            )) } }
            .doOnSuccess {
                userStatisticRepository.createStatistic(it).blockingSubscribe()
            }
    }

    private fun getClearAdded(statistics: List<UserStatistic>): Int{
        val addedIds = mutableMapOf<Long, UserStatistic>()

        statistics.forEach { day ->
            day.added.forEach {
                addedIds[it] = day
            }
        }

        statistics.forEach { day ->
            day.deleted.forEach {
                addedIds.remove(it)
            }
        }

        return addedIds.size
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
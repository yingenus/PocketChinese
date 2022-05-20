package com.yingenus.pocketchinese.domain.entities.studystatictic

import com.yingenus.pocketchinese.domain.dto.*
import com.yingenus.pocketchinese.domain.dto.UserStatistic
import com.yingenus.pocketchinese.domain.entities.repeat.Expired
import com.yingenus.pocketchinese.domain.entities.repeat.RepeatHelper
import com.yingenus.pocketchinese.domain.repository.StudyRepository
import com.yingenus.pocketchinese.domain.repository.TrainingRepository
import com.yingenus.pocketchinese.domain.repository.UserStatisticRepository
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class ListStatisticImpl @Inject constructor(
    private val studyRepository: StudyRepository,
    private val trainingRepository: TrainingRepository,
    private val userStatisticRepository: UserStatisticRepository,
    private val repeatHelper: RepeatHelper
) : ListStatistic {

    private var repeatType : RepeatType  = RepeatType.default

    override fun setRepeatType(repeatType: RepeatType) {
        this.repeatType = repeatType
    }

    override fun getStatistic(studyList: StudyList): Single<StudyListStatistic> {
        return Single.zip(
            studyRepository.getStudyWordsByListId(studyList.id).observeOn(Schedulers.computation()),
            trainingRepository.getTrainingCondForList(studyList.id).observeOn(Schedulers.computation()),
            userStatisticRepository.getStatisticsLast(TimePeriod.ONE_MONTH).observeOn(Schedulers.computation()),
        ){ words, trenings, ststistics ->
            StudyListStatistic(
                words = words.size,
                lastRepeat = getLastRepeatDate(trenings),
                nextRepeat = getNextRepeatDate(trenings),
                successChn = if(trenings.isNotEmpty())(trenings.map { it.chineseLevel.level }.reduce { acc, i -> acc + i }* 100) / KnownLevel.maxLevel.level else 0,
                successPin = if(trenings.isNotEmpty()) (trenings.map { it.pinyinLevel.level }.reduce { acc, i -> acc + i }* 100) / KnownLevel.maxLevel.level else 0,
                successTrn = if(trenings.isNotEmpty()) (trenings.map { it.translationLevel.level }.reduce { acc, i -> acc + i }* 100) / KnownLevel.maxLevel.level else 0,
                repeat = getRepeatRecomend(trenings),
                percentComplete = getPercentComplete(trenings),
                repeatedWords = getRepeatedWords(ststistics,words),
                addedWords = getAddedWord(ststistics,words)
            )
        }
    }

    override fun getShortStatistic(studyList: StudyList): Single<StudyListStatisticShort> {
        return Single.zip(
            studyRepository.getStudyWordsByListId(studyList.id).observeOn(Schedulers.computation()),
            trainingRepository.getTrainingCondForList(studyList.id).observeOn(Schedulers.computation())
        ){ words, trenings ->
            StudyListStatisticShort(
                words = words.size,
                repeat = getRepeatRecomend(trenings),
                percentComplete = getPercentComplete(trenings),
                nextRepeat = getNextRepeatDate(trenings)
            )
        }
    }

    private fun getLastRepeatDate(trainingConds : List<TrainingCond>): Date?{
        val dates = mutableListOf<Date>()

        if (!repeatType.ignoreCHN)
            dates.addAll(trainingConds.map { it.trainingDateChinese })
        if (!repeatType.ignorePIN)
            dates.addAll(trainingConds.map { it.trainingDatePinyin })
        if (!repeatType.ignoreTRN)
            dates.addAll(trainingConds.map { it.trainingDateTranslation })

        return dates.maxOrNull()
    }

    private fun getNextRepeatDate(trainingConds : List<TrainingCond>) : Date?{
        val dates = mutableListOf<Date>()

        if (!repeatType.ignoreCHN)
            dates.addAll(trainingConds
                .map { repeatHelper.nextRepeat(it.trainingDateChinese,it.chineseLevel) })
        if (!repeatType.ignorePIN)
            dates.addAll(trainingConds
                .map { repeatHelper.nextRepeat(it.trainingDatePinyin,it.pinyinLevel)})
        if (!repeatType.ignoreTRN)
            dates.addAll(trainingConds
                .map { repeatHelper.nextRepeat(it.trainingDateTranslation,it.translationLevel) })

        return dates.minOrNull()
    }

    private fun getRepeatRecomend(trainingConds : List<TrainingCond>) : RepeatRecomend{
        var repeatRecomends = mutableSetOf<Expired>()

        if (!repeatType.ignoreCHN) {
            repeatRecomends.addAll(trainingConds
                .map {
                    repeatHelper.howExpired(
                        it.trainingDateChinese,
                        it.chineseLevel)
                })
            if (trainingConds.any { it.trainingStatusChinese == TrainingStatus.FILED }) repeatRecomends.add(Expired.BED)
        }
        if (!repeatType.ignorePIN) {
            repeatRecomends.addAll(trainingConds
                .map {
                    repeatHelper.howExpired(
                        it.trainingDatePinyin,
                        it.pinyinLevel)
                })
            if (trainingConds.any { it.trainingStatusPinyin == TrainingStatus.FILED }) repeatRecomends.add(Expired.BED)
        }
        if (!repeatType.ignoreTRN) {
            repeatRecomends.addAll(trainingConds
                .map {
                    repeatHelper.howExpired(
                        it.trainingDateTranslation,
                        it.translationLevel
                    )
                })
            if (trainingConds.any { it.trainingStatusTranslation == TrainingStatus.FILED }) repeatRecomends.add(Expired.BED)
        }

        return if (repeatRecomends.contains(Expired.BED)) RepeatRecomend.NEED
        else if (repeatRecomends.contains(Expired.MEDIUM)) RepeatRecomend.SHOULD
        else RepeatRecomend.DONT_NEED
    }

    private fun getPercentComplete(trainingConds : List<TrainingCond>) : Int{
        val levls = mutableListOf<Int>()

        if (!repeatType.ignoreCHN)
            levls.addAll(trainingConds.map { it.chineseLevel.level })
        if (!repeatType.ignorePIN)
            levls.addAll(trainingConds.map { it.pinyinLevel.level })
        if (!repeatType.ignoreTRN)
            levls.addAll(trainingConds.map { it.translationLevel.level })

        return if (levls.isNotEmpty()) levls.reduce { acc, i -> acc + i } / levls.size else 0
    }

    private fun getRepeatedWords(statistics : List<UserStatistic>, words : List<StudyWord>) : Int{
        val idsMap = mutableMapOf<Long,StudyWord>()

        words.forEach { idsMap[it.id] = it }

        var count = 0

        statistics.forEach { day ->
            day.repeated.forEach {
                if (idsMap.containsKey(it)) count++
            }
        }
        return count
    }

    private fun getAddedWord(statistics : List<UserStatistic>, words : List<StudyWord>) : Int{
        val idsMap = mutableMapOf<Long,StudyWord>()

        words.forEach { idsMap[it.id] = it }

        var count = 0

        statistics.forEach { day ->
            day.added.forEach {
                if (idsMap.containsKey(it)) count++
            }
        }
        return count
    }

}
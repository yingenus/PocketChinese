package com.yingenus.pocketchinese.domain.entities.studystatictic

import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.domain.dto.*
import com.yingenus.pocketchinese.domain.entities.repeat.Expired
import com.yingenus.pocketchinese.domain.entities.repeat.RepeatHelper
import com.yingenus.pocketchinese.domain.repository.TrainingRepository
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class WordStatisticImpl @Inject constructor(
    private val trainingRepository: TrainingRepository,
    private val repeatHelper: RepeatHelper
) : WordStatistic {

    private var repeatType : RepeatType = RepeatType.default

    override fun setRepeatType(repeatType: RepeatType) {
        this.repeatType = repeatType
    }

    override fun getStatistic(word: StudyWord): Single<StudyWordStatistic> {
        return Maybe.defer { trainingRepository.getTrainingCond(word.id) }
            .observeOn(Schedulers.computation())
            .map {
                StudyWordStatistic(
                    wordSuccess = getWordLevel(it),
                    chineseSuccessPercent = it.chineseLevel.level / KnownLevel.maxLevel.level,
                    pinyinSuccessPercent = it.pinyinLevel.level / KnownLevel.maxLevel.level,
                    translationSuccessPercent =  it.translationLevel.level / KnownLevel.maxLevel.level,
                    recomend = getRepeatRecomend(it)
                )
            }
            .defaultIfEmpty(
                StudyWordStatistic(0,0,0,0,RepeatRecomend.DONT_NEED)
            )
    }

    override fun getStatisticByLanguage(word: StudyWord): Single<StudyWordStatisticByLanguage> {
        return Maybe.defer { trainingRepository.getTrainingCond(word.id) }
            .observeOn(Schedulers.computation())
            .map {
                StudyWordStatisticByLanguage(
                    wordSuccess = getWordLevel(it),
                    chineseSuccessPercent = it.chineseLevel.level / KnownLevel.maxLevel.level,
                    pinyinSuccessPercent = it.pinyinLevel.level / KnownLevel.maxLevel.level,
                    translationSuccessPercent =  it.translationLevel.level / KnownLevel.maxLevel.level,
                    repeatChinese = getRepeatRecomend(it,Language.CHINESE),
                    repeatPinyin = getRepeatRecomend(it,Language.PINYIN),
                    repeatTranslation = getRepeatRecomend(it,Language.RUSSIAN),
                    repeatStatusChinese = it.trainingStatusChinese,
                    repeatStatusPinyin = it.trainingStatusPinyin,
                    repeatStatusTranslation = it.trainingStatusTranslation
                )
            }
            .defaultIfEmpty(
                StudyWordStatisticByLanguage(
                    0,
                    0,
                    0,
                    0,
                    RepeatRecomend.DONT_NEED,
                    RepeatRecomend.DONT_NEED,
                    RepeatRecomend.DONT_NEED,
                    TrainingStatus.SUCCESS,
                    TrainingStatus.SUCCESS,
                    TrainingStatus.SUCCESS)
            )
    }

    private fun getWordLevel(trainingCond : TrainingCond) : Int{
        val levels = mutableListOf<Int>()

        if (!repeatType.ignoreCHN)
            levels.add(trainingCond.chineseLevel.level)
        if (!repeatType.ignorePIN)
            levels.add(trainingCond.pinyinLevel.level)
        if (!repeatType.ignoreTRN)
            levels.add(trainingCond.translationLevel.level)

        return Math.ceil((levels.reduce { acc, i -> acc + i }.toDouble() / levels.size)).toInt()
    }

    private fun getRepeatRecomend(trainingCond : TrainingCond) : RepeatRecomend {
        val repeatRecomends = mutableSetOf<Expired>()

        if (!repeatType.ignoreCHN) {
            repeatRecomends.add(
                repeatHelper.howExpired(
                    trainingCond.trainingDateChinese,
                    trainingCond.chineseLevel
                )
            )
            if (trainingCond.trainingStatusChinese == TrainingStatus.FILED) repeatRecomends.add(Expired.BED)
        }
        if (!repeatType.ignorePIN) {
            repeatRecomends.add(
                repeatHelper.howExpired(
                    trainingCond.trainingDatePinyin,
                    trainingCond.pinyinLevel
                )
            )
            if (trainingCond.trainingStatusPinyin == TrainingStatus.FILED) repeatRecomends.add(Expired.BED)
        }
        if (!repeatType.ignoreTRN){
            repeatRecomends.add(
                repeatHelper.howExpired(
                    trainingCond.trainingDateTranslation,
                    trainingCond.translationLevel
                )
            )
            if (trainingCond.trainingStatusTranslation == TrainingStatus.FILED) repeatRecomends.add(Expired.BED)
        }

        return if (repeatRecomends.contains(Expired.BED)) RepeatRecomend.NEED
        else if (repeatRecomends.contains(Expired.MEDIUM)) RepeatRecomend.SHOULD
        else RepeatRecomend.DONT_NEED
    }

    private fun getRepeatRecomend(trainingCond : TrainingCond, language: Language) : RepeatRecomend {
        val repeatRecomends = mutableSetOf<Expired>()

        when(language){
            Language.CHINESE ->{
                if (!repeatType.ignoreCHN) {
                    repeatRecomends.add(
                        repeatHelper.howExpired(
                            trainingCond.trainingDateChinese,
                            trainingCond.chineseLevel
                        )
                    )
                    if (trainingCond.trainingStatusChinese == TrainingStatus.FILED) repeatRecomends.add(Expired.BED)
                }
                else {
                    repeatRecomends.add(Expired.GOOD)
                }
            }
            Language.PINYIN ->{
                if (!repeatType.ignorePIN) {
                    repeatRecomends.add(
                        repeatHelper.howExpired(
                            trainingCond.trainingDatePinyin,
                            trainingCond.pinyinLevel
                        )
                    )
                    if (trainingCond.trainingStatusPinyin == TrainingStatus.FILED) repeatRecomends.add(Expired.BED)
                }
                else{
                    repeatRecomends.add(Expired.GOOD)
                }
            }
            Language.RUSSIAN ->{
                if (!repeatType.ignoreTRN){
                    repeatRecomends.add(
                        repeatHelper.howExpired(
                            trainingCond.trainingDateTranslation,
                            trainingCond.translationLevel
                        )
                    )
                    if (trainingCond.trainingStatusTranslation == TrainingStatus.FILED) repeatRecomends.add(Expired.BED)
                }
                else{
                    repeatRecomends.add(Expired.GOOD)
                }
            }
        }

        return if (repeatRecomends.contains(Expired.BED)) RepeatRecomend.NEED
        else if (repeatRecomends.contains(Expired.MEDIUM)) RepeatRecomend.SHOULD
        else RepeatRecomend.DONT_NEED
    }
}
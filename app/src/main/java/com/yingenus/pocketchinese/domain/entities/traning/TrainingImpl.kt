package com.yingenus.pocketchinese.domain.entities.traning

import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.domain.dto.*
import com.yingenus.pocketchinese.domain.entities.repeat.RepeatHelper
import com.yingenus.pocketchinese.domain.repository.TrainingRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class TrainingImpl @Inject constructor(
    private val trainingRepository: TrainingRepository,
    private val repeatHelper: RepeatHelper) : Training {

    private lateinit var language : Language

    private val decrement : (TrainingCond) -> Int = {
        val mistakes = mistake(it)
        if (mistakes > 16) 4
        else if (mistakes > 11) 3
        else if (mistakes > 6) 2
        else if (mistakes > 2) 1
        else 0
    }

    private val mistake : (TrainingCond) -> Int ={
        when(language){
            Language.RUSSIAN -> it.trainingCountTranslation
            Language.PINYIN -> it.trainingCountPinyin
            Language.CHINESE -> it.trainingCountChinese
        }
    }

    private val answerChecker : AnswerChecker = CheckerFactory.getAnswerChecker(language)

    override fun setLanguage(language: Language) {
        this.language = language
    }

    override fun postAnswer(answer: String, word: StudyWord): Single<TrainedResult> {
        check( ::language.isInitialized, { "languge should be initialized"})
        return trainingRepository.getTrainingCond(word.id)
            .switchIfEmpty ( word.createTrainingCond() )
            .map {
                val result = answerChecker.check(word,answer)
                val oldState = it.trainingStatus()

                val action = if (result) it.doOnSuccess() else it.doOnFiled()
                action.blockingSubscribe()

                if (result){
                    if (oldState == TrainingStatus.FILED) TrainedResult.SUCCESS_AFTER_FILED
                    else TrainedResult.SUCCESS
                }else{
                    TrainedResult.FILED
                }
            }
            .observeOn(Schedulers.computation())

    }

    override fun showAnswer(word: StudyWord): Single<String> {
        check( ::language.isInitialized, { "languge should be initialized"})
        return trainingRepository
            .getTrainingCond(word.id)
            .switchIfEmpty ( word.createTrainingCond() )
            .doOnSuccess {
                it.setTrainingStatus(TrainingStatus.FILED)
                trainingRepository.updateTrainingCond(it).blockingSubscribe()
            }
            .observeOn(Schedulers.computation())
            .ignoreElement()
            .toSingle { word.correctAnswer() }
    }

    private fun TrainingCond.doOnSuccess() : Completable =
        Single.create<TrainingCond> { it.onSuccess(this) }
            .doOnSuccess {
                if (it.trainingStatus() == TrainingStatus.SUCCESS){
                    it.setTrainingStatus(TrainingStatus.SUCCESS)

                    if(repeatHelper.canAccept(it.trainingDate(),it.level())){
                        it.setLevel( it.level().inc())
                    }
                }
                else{
                    val dec = decrement(it)
                    it.setTrainingStatus(TrainingStatus.SUCCESS)
                    it.setLevel(KnownLevel.creteSafe(
                        it.level().level - dec
                    ))
                }
                it.setTrainingCount(0)
                it.setTrainingDate(Date(System.currentTimeMillis()))
                trainingRepository.updateTrainingCond(it).blockingSubscribe()
            }.ignoreElement()

    private fun TrainingCond.doOnFiled() : Completable = Single.create<TrainingCond> { it.onSuccess(this) }
        .doOnSuccess {
            it.setTrainingStatus(TrainingStatus.FILED)
            it.setTrainingCount( it.trainingCount()+1)
            it.setTrainingDate(Date(System.currentTimeMillis()))
            trainingRepository.updateTrainingCond(it).blockingSubscribe()
        }.ignoreElement()

    private fun StudyWord.createTrainingCond(): Single<TrainingCond> =
        Single.defer{ Single.create<TrainingCond> {
            it.onSuccess(TrainingCond.creteEmpty(this))
        }}.doOnSuccess {
            trainingRepository.creteTrainingCond(it).blockingSubscribe()
        }

    private fun StudyWord.correctAnswer() = when(language){
        Language.RUSSIAN -> this.translate
        Language.CHINESE -> this.chinese
        Language.PINYIN -> this.pinyin
    }

    private fun TrainingCond.trainingStatus() =
        when(language){
            Language.RUSSIAN -> this.trainingStatusTranslation
            Language.PINYIN -> this.trainingStatusPinyin
            Language.CHINESE -> this.trainingStatusChinese
        }
    private fun TrainingCond.setTrainingStatus( trainingStatus: TrainingStatus) =
        when(language){
            Language.RUSSIAN -> this.trainingStatusTranslation = trainingStatus
            Language.PINYIN -> this.trainingStatusPinyin  = trainingStatus
            Language.CHINESE -> this.trainingStatusChinese = trainingStatus
        }
    private fun TrainingCond.setTrainingDate ( date: Date) = when(language){
            Language.RUSSIAN -> this.trainingDateTranslation = date
            Language.PINYIN -> this.trainingDatePinyin  = date
            Language.CHINESE -> this.trainingDateChinese = date
        }
    private fun TrainingCond.trainingDate() = when(language){
        Language.RUSSIAN -> this.trainingDateTranslation
        Language.PINYIN -> this.trainingDatePinyin
        Language.CHINESE -> this.trainingDateChinese
    }
    private fun TrainingCond.level() =
        when(language){
            Language.RUSSIAN -> this.translationLevel
            Language.PINYIN -> this.pinyinLevel
            Language.CHINESE -> this.chineseLevel
        }
    private fun TrainingCond.setLevel( lvl : KnownLevel) =
        when(language){
            Language.RUSSIAN -> this.translationLevel = lvl
            Language.PINYIN -> this.pinyinLevel = lvl
            Language.CHINESE -> this.chineseLevel = lvl
        }
    private fun TrainingCond.trainingCount() = when(language){
        Language.RUSSIAN -> this.trainingCountTranslation
        Language.PINYIN -> this.trainingCountPinyin
        Language.CHINESE -> this.trainingCountChinese
    }
    private fun TrainingCond.setTrainingCount(count : Int) =
        when(language){
            Language.RUSSIAN -> this.trainingCountTranslation = count
            Language.PINYIN -> this.trainingCountPinyin = count
            Language.CHINESE -> this.trainingCountChinese = count
        }

}
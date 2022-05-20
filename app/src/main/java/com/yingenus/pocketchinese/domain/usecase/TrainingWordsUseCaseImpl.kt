package com.yingenus.pocketchinese.domain.usecase

import com.yingenus.pocketchinese.ISettings
import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.domain.dto.*
import com.yingenus.pocketchinese.domain.entities.studystatictic.UserStatistics
import com.yingenus.pocketchinese.domain.entities.studystatictic.WordStatistic
import com.yingenus.pocketchinese.domain.entities.traning.Training
import com.yingenus.pocketchinese.domain.repository.StudyRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

class TrainingWordsUseCaseImpl @Inject constructor(
    private val training: Training,
    private val studyRepository: StudyRepository,
    private val userStatistics: UserStatistics,
    private val studyWordStatistic: WordStatistic,
    setting : ISettings
) : TrainingWordsUseCase {

    init {
        studyWordStatistic.setRepeatType(setting.getRepeatType())
    }

    private lateinit var trainingConf: TrainingConf

    private val allWordsPublish : PublishSubject<Int> = PublishSubject.create()
    private var all : Int = 0
        set(value) {
            allWordsPublish.onNext(value)
            field = value
        }
    private val badWordsPublish : PublishSubject<Int> = PublishSubject.create()
    private var bad : Int = 0
        set(value) {
            badWordsPublish.onNext(value)
            field = value
        }
    private val goodWordsPublish : PublishSubject<Int> = PublishSubject.create()
    private var good : Int = 0
        set(value) {
            allWordsPublish.onNext(value)
            field = value
        }

    private val trainingStatisticObserver = Observable.combineLatest(
        allWordsPublish,badWordsPublish,goodWordsPublish){all, bad, good -> TrainingStatistic(good,bad,all)}
        .publish()
        .autoConnect()

    private lateinit var trainedSet : MutableSet<StudyWord>

    override fun init(trainingConfig: TrainingConf): Completable {
        check(!::trainingConf.isInitialized){ " TrainingConf cant be seted twise"}

        return studyRepository.getStudyWordsByListId(trainingConfig.studyListId)
            .flatMapObservable { Observable.fromIterable(it) }.flatMap { word ->
                studyWordStatistic.getStatisticByLanguage(word).map { word to it }.toObservable()
            }.filter {
                if (trainingConfig.trainingWords == TrainingConf.TrainingWords.ALL){
                    true
                }
                else{
                    when(trainingConfig.language){
                        Language.CHINESE -> it.second.repeatChinese != RepeatRecomend.DONT_NEED
                        Language.PINYIN -> it.second.repeatPinyin != RepeatRecomend.DONT_NEED
                        Language.RUSSIAN -> it.second.repeatTranslation != RepeatRecomend.DONT_NEED
                    }
                }
            }
            .collect({ mutableListOf<Pair<StudyWord,StudyWordStatisticByLanguage>>()},{ list, item ->
                list.add(item)
            })
            .doOnSuccess {
                trainedSet = mutableSetOf<StudyWord>()
                trainedSet.addAll(it.map { it.first })
                all = it.size
                //allWordsPublish.onNext(it.size)
                good = 0
                //goodWordsPublish.onNext(0)
                bad = it.map {
                    when(trainingConfig.language){
                        Language.CHINESE -> it.second.repeatStatusChinese
                        Language.PINYIN -> it.second.repeatStatusPinyin
                        Language.RUSSIAN -> it.second.repeatStatusTranslation
                    }
                }.filter { it == TrainingStatus.FILED }.size
                //badWordsPublish.onNext()
                this.trainingConf = trainingConfig
                training.setLanguage(trainingConfig.language)
            }.ignoreElement()
    }

    override fun getTrainingWords(): Single<List<StudyWord>> {
        check(::trainingConf.isInitialized){ " TrainingConf must be set"}
        return Single.defer {
            Single.create<List<StudyWord>> {
                it.onSuccess(trainedSet.toList())
            }
        }
    }

    override fun getTrainingStatistic(): Observable<TrainingStatistic> {
        check(::trainingConf.isInitialized){ " TrainingConf must be set"}
        return trainingStatisticObserver
    }

    override fun showAnswer(studyWord: StudyWord): Single<String> {
        check(::trainingConf.isInitialized){ " TrainingConf must be set"}
        return training.showAnswer(studyWord)
            .doOnSuccess {
                userStatistics.wordTrained(studyWord.id, TrainedResult.SHOWED, trainingConf.language)
            }
    }

    override fun postAnswer(answer: String, studyWord: StudyWord): Single<Boolean> {
        check(::trainingConf.isInitialized){ " TrainingConf must be set"}
        return training.postAnswer(answer,studyWord)
            .doOnSuccess {
                userStatistics.wordTrained(
                    id = studyWord.id,
                    trainedResult = it,
                    language = trainingConf.language
                )
                when(it){
                    TrainedResult.SUCCESS -> good++
                    TrainedResult.SUCCESS_AFTER_FILED -> {
                        bad --
                        good ++
                    }
                    TrainedResult.FILED -> {
                        bad ++
                    }
                    TrainedResult.SHOWED ->{
                        bad ++
                    }
                }
            }
            .map { it == TrainedResult.SUCCESS || it == TrainedResult.SUCCESS_AFTER_FILED }
            .doOnSuccess {
                if (it) trainedSet.remove(studyWord)
            }
    }
}
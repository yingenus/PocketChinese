package com.yingenus.pocketchinese.domain.usecase

import com.yingenus.pocketchinese.ISettings
import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.domain.dto.*
import com.yingenus.pocketchinese.domain.entities.studystatictic.WordStatistic
import com.yingenus.pocketchinese.domain.repository.RepeatStatistic
import com.yingenus.pocketchinese.domain.repository.StudyRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class TrainedWordsUseCaseImpl @Inject constructor(
    val wordStatistic: WordStatistic,
    val studyRepository: StudyRepository,
    settings : ISettings
) : TrainedWordsUseCase {

    val repeatType = settings.getRepeatType()

    init {
        wordStatistic.setRepeatType(repeatType)
    }

    companion object{
        private val filed : (Language, StudyWordStatisticByLanguage) -> Int = { language, studyWordStatisticByLanguage ->
            when(language){
                Language.CHINESE -> if(studyWordStatisticByLanguage.repeatStatusChinese == TrainingStatus.FILED) 1 else 0
                Language.PINYIN -> if(studyWordStatisticByLanguage.repeatStatusPinyin == TrainingStatus.FILED) 1 else 0
                Language.RUSSIAN -> if(studyWordStatisticByLanguage.repeatStatusTranslation == TrainingStatus.FILED) 1 else 0
            }
        }
        private val repeated : (Language, StudyWordStatisticByLanguage) -> Int = { language, studyWordStatisticByLanguage ->
            when(language){
                Language.CHINESE -> if(studyWordStatisticByLanguage.repeatChinese != RepeatRecomend.DONT_NEED) 1 else 0
                Language.PINYIN -> if(studyWordStatisticByLanguage.repeatPinyin != RepeatRecomend.DONT_NEED) 1 else 0
                Language.RUSSIAN -> if(studyWordStatisticByLanguage.repeatTranslation != RepeatRecomend.DONT_NEED) 1 else 0
            }
        }
    }

    override fun getTrainedWords(language: Language, userListId : Long): Single<TrainedWords> {
        return studyRepository
            .getStudyWordsByListId(userListId)
            .flatMapObservable {
                Observable.fromIterable(it)
            }
            .flatMap {
                wordStatistic.getStatisticByLanguage(it).toObservable()
            }
            .collect({ TrainedWords(0,0,0)}, {trained, statistic->
                trained.filed += filed(language,statistic)
                trained.repeatable += repeated(language,statistic)
                trained.all += 1
            })
    }




}
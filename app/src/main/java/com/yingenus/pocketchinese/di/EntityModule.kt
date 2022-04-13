package com.yingenus.pocketchinese.di

import com.yingenus.pocketchinese.domain.entities.namestandards.StudyWordsStandards
import com.yingenus.pocketchinese.domain.entities.namestandards.StudyWordsStandardsImpl
import com.yingenus.pocketchinese.domain.entities.repeat.FibRepeatScope
import com.yingenus.pocketchinese.domain.entities.repeat.RepeatHelper
import com.yingenus.pocketchinese.domain.entities.repeat.RepeatHelperNew
import com.yingenus.pocketchinese.domain.entities.studystatictic.*
import com.yingenus.pocketchinese.domain.entities.traning.Training
import com.yingenus.pocketchinese.domain.entities.traning.TrainingImpl
import dagger.Module
import dagger.Provides

@Module( includes = [RepositoryModule::class])
class EntityModule {

    @Provides
    fun provideRepeatHelper():RepeatHelper = RepeatHelperNew(FibRepeatScope())

    @Provides
    fun provideTraining( trainingImpl: TrainingImpl) : Training = trainingImpl

    @Provides
    fun provideUserStatistic( userStatisticImpl: UserStatisticImpl): UserStatistics = userStatisticImpl

    @Provides
    fun provideListStatistic( listStatisticImpl: ListStatisticImpl) : ListStatistic = listStatisticImpl

    @Provides
    fun provideWordStatistic( wordStatisticImpl: WordStatisticImpl): WordStatistic = wordStatisticImpl

    @Provides
    fun provideStudyWordsStandards( studyWordsStandards : StudyWordsStandardsImpl) : StudyWordsStandards = studyWordsStandards
}
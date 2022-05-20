package com.yingenus.pocketchinese.di

import com.yingenus.pocketchinese.domain.usecase.*
import dagger.Binds
import dagger.Module

@Module(includes = [RepositoryModule::class, SearchModule::class, EntityModule::class])
abstract class UseCaseModule {

    @Binds
    abstract fun provideWordsSearchUseCase( wordSearchUseCaseImpl: WordSearchUseCaseImpl): WordsSearchUseCase
    @Binds
    abstract fun provideModifyStudyListUseCase( modifyStudyListUseCaseImpl : ModifyStudyListUseCaseImpl) : ModifyStudyListUseCase
    @Binds
    abstract fun provideModifyStudyWordUseCase( modifyStudyWordUseCaseImpl : ModifyStudyWordUseCaseImpl) : ModifyStudyWordUseCase
    @Binds
    abstract fun provideStudyListInfoUseCase( studyListInfoUseCaseImpl : StudyListInfoUseCaseImpl) : StudyListInfoUseCase
    @Binds
    abstract fun provideTrainingWordsUseCase( trainingWordsUseCaseImpl : TrainingWordsUseCaseImpl): TrainingWordsUseCase
    @Binds
    abstract fun provideUserStatisticUseCase( userStatisticUseCaseImpl : UserStatisticUseCaseImpl): UserStatisticUseCase
    @Binds
    abstract fun provideWordInfoUseCase( wordInfoUseCaseImpl : WordInfoUseCaseImpl): WordInfoUseCase
    @Binds
    abstract fun provideAddSuggestWordsToStudyList( addSuggestWordsToStudyListImpl : AddSuggestWordsToStudyListImpl): AddSuggestWordsToStudyList
    @Binds
    abstract fun provideTrainedWordsUseCase(trainingWordsUseCaseImpl: TrainedWordsUseCaseImpl): TrainedWordsUseCase

}
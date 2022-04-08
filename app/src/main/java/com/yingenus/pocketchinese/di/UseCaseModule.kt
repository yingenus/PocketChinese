package com.yingenus.pocketchinese.di

import com.yingenus.pocketchinese.domain.usecase.WordSearchUseCaseImpl
import com.yingenus.pocketchinese.domain.usecase.WordsSearchUseCase
import dagger.Binds
import dagger.Module

@Module(includes = [RepositoryModule::class, SearchModule::class])
abstract class UseCaseModule {

    @Binds
    abstract fun provideWordsSearchUseCase( wordSearchUseCaseImpl: WordSearchUseCaseImpl): WordsSearchUseCase
}
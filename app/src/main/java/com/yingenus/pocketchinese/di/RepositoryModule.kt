package com.yingenus.pocketchinese.di

import com.yingenus.pocketchinese.data.assets.GrammarAssetsRepository
import com.yingenus.pocketchinese.data.assets.ImageAssetsRepository
import com.yingenus.pocketchinese.data.local.RoomExampleRepository
import com.yingenus.pocketchinese.data.local.RoomWordRepository
import com.yingenus.pocketchinese.domain.repository.*
import dagger.Module
import dagger.Binds

@Module(includes = [RoomModule::class, AssetsModule::class])
abstract class RepositoryModule() {

    @Binds
    abstract fun provideChinCharRepository( roomWordRepository: RoomWordRepository): ChinCharRepository
    @Binds
    abstract fun provideExampleRepository( roomExampleRepository: RoomExampleRepository): ExampleRepository
    @Binds
    abstract fun provideRadicalsRepository( roomWordRepository: RoomWordRepository) : RadicalsRepository
    @Binds
    abstract fun provideToneRepository( roomWordRepository: RoomWordRepository): ToneRepository
    @Binds
    abstract fun provideGrammarRep( grammarAssetsRepository: GrammarAssetsRepository): GrammarRep
    @Binds
    abstract fun provideImageRep( imageAssetsRepository: ImageAssetsRepository):ImageRep

}
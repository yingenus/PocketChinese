package com.yingenus.pocketchinese.di

import com.yingenus.pocketchinese.data.assets.GrammarAssetsRepository
import com.yingenus.pocketchinese.data.assets.ImageAssetsRepository
import com.yingenus.pocketchinese.data.json.suggest.AssetsJSONSuggestWordsRepository
import com.yingenus.pocketchinese.data.local.RoomPocketRepository
import com.yingenus.pocketchinese.data.proxy.ProxyDictionaryItemRepository
import com.yingenus.pocketchinese.data.proxy.ProxyExampleRepository
import com.yingenus.pocketchinese.data.proxy.ProxyRadicalsRepository
import com.yingenus.pocketchinese.data.proxy.ProxyToneRepository
import com.yingenus.pocketchinese.domain.repository.*
import dagger.Module
import dagger.Binds

@Module(includes = [ProxyRepositoryModule::class, AssetsModule::class, RoomModule::class])
abstract class RepositoryModule() {

    @Binds
    abstract fun provideDictionaryItemRepository( rep : ProxyDictionaryItemRepository): DictionaryItemRepository
    @Binds
    abstract fun provideExampleRepository( rep : ProxyExampleRepository): ExampleRepository
    @Binds
    abstract fun provideRadicalsRepository( rep : ProxyRadicalsRepository) : RadicalsRepository
    @Binds
    abstract fun provideToneRepository( rep : ProxyToneRepository): ToneRepository
    @Binds
    abstract fun provideGrammarRep( grammarAssetsRepository: GrammarAssetsRepository): GrammarRep
    @Binds
    abstract fun provideImageRep( imageAssetsRepository: ImageAssetsRepository):ImageRep
    @Binds
    abstract fun provideStudyRepository ( roomPocketRepository: RoomPocketRepository) : StudyRepository
    @Binds
    abstract fun provideTrainingRepository (roomPocketRepository: RoomPocketRepository) : TrainingRepository
    @Binds
    abstract fun provideUserStatisticRepository( roomPocketRepository: RoomPocketRepository) : UserStatisticRepository
    @Binds
    abstract fun provideSuggestWordsRepository( assetsJSONSuggestWordsRepository: AssetsJSONSuggestWordsRepository): SuggestWordsRepository

}
package com.yingenus.pocketchinese.di

import com.yingenus.pocketchinese.data.assets.GrammarAssetsRepository
import com.yingenus.pocketchinese.data.assets.ImageAssetsRepository
import com.yingenus.pocketchinese.data.local.RoomExampleRepository
import com.yingenus.pocketchinese.data.local.RoomWordRepository
import com.yingenus.pocketchinese.data.proxy.*
import com.yingenus.pocketchinese.domain.dto.VariantWord
import com.yingenus.pocketchinese.domain.repository.*
import com.yingenus.pocketchinese.domain.repository.search.NgramRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class ProxyRepositoryModule {

    @Provides
    fun provideDictionaryItemRepository( proxyProvider : ProxyRepositoryProvider): ProxyDictionaryItemRepository =
        proxyProvider.proxyDictionaryItemRepository
    @Provides
    fun provideExampleRepository( proxyProvider : ProxyRepositoryProvider): ProxyExampleRepository =
        proxyProvider.proxyExampleRepository
    @Provides
    fun provideRadicalsRepository( proxyProvider : ProxyRepositoryProvider) : ProxyRadicalsRepository =
        proxyProvider.proxyRadicalsRepository
    @Provides
    fun provideToneRepository( proxyProvider : ProxyRepositoryProvider): ProxyToneRepository =
        proxyProvider.proxyToneRepository

    @Provides
    @Named("chinese_1gram_repo")
    fun provideChinN1Repository( proxyProvider : ProxyRepositoryProvider): NgramRepository<VariantWord> =
        proxyProvider.proxyChinN1Repository

    @Provides
    @Named("chinese_2gram_repo")
    fun provideChinN2Repository( proxyProvider : ProxyRepositoryProvider): NgramRepository<VariantWord> =
        proxyProvider.proxyChinN2Repository

}
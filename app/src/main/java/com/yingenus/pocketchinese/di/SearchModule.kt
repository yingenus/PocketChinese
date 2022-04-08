package com.yingenus.pocketchinese.di

import com.yingenus.pocketchinese.domain.dto.VariantWord
import com.yingenus.pocketchinese.domain.entities.dictionarysearch.*
import com.yingenus.pocketchinese.domain.entities.dictionarysearch.ngram.NgramSearch
import com.yingenus.pocketchinese.domain.repository.search.NgramRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import main.newsearch.NGramSearch.NgramM1Search
import javax.inject.Named

@Module(includes = [RepositoryModule::class, ProxyRepositoryModule::class])
class SearchModule {

    @Provides
    @Named("match_search")
    fun provideMatchSearch(searchEngine: MatchSearchEngine): SearchEngine = searchEngine

    @Provides
    @Named("fuzzy_search")
    fun provideFuzzySearch(searchEngine: FuzzySearchEngine) : SearchEngine = searchEngine

    @Provides
    @Named("chinese_searcher")
    fun provideChineseSearcher(
        @Named("chinese_1_gram")chinN1 : NgramSearch<VariantWord>,
        @Named("chinese_2_gram")chinN2 :NgramSearch<VariantWord>): Searcher {
        return TwoNgramsSearch(chinN1,chinN2)
    }

    @Provides
    @Named("pinyin_searcher")
    fun providePinyinSearcher( provider: ProxySearcherProvider) : Searcher =
        provider.pinyinProxySearcher

    @Provides
    @Named("russian_searcher")
    fun provideRussianSearcher( provider: ProxySearcherProvider) : Searcher =
        provider.russianProxySearcher

    @Provides
    @Named("chinese_1_gram")
    fun provideN1Searcher(
        @Named("chinese_1gram_repo") repository :NgramRepository<VariantWord>): NgramSearch<VariantWord> {

        return NgramM1Search<VariantWord>( repository, 1)
    }

    @Provides
    @Named("chinese_2_gram")
    fun provideN2Searcher(
        @Named("chinese_2gram_repo") repository :NgramRepository<VariantWord>): NgramSearch<VariantWord> {

        return NgramM1Search<VariantWord>( repository, 2)
    }

}
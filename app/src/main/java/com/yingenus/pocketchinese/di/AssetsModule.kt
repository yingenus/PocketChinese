package com.yingenus.pocketchinese.di

import android.content.Context
import com.yingenus.pocketchinese.data.assets.GrammarAssetsRepository
import com.yingenus.pocketchinese.data.assets.ImageAssetsRepository
import com.yingenus.pocketchinese.data.json.suggest.AssetsJSONSuggestWordsRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AssetsModule {
    @Provides
    @Singleton
    fun provideGrammarAssetsRepository( context: Context): GrammarAssetsRepository {
        return GrammarAssetsRepository(context)
    }
    @Provides
    @Singleton
    fun provideImageAssetsRepository(context: Context): ImageAssetsRepository{
        return ImageAssetsRepository(context)
    }
    @Provides
    @Singleton
    fun provideAssetsJSONSuggestWordsRepository(context: Context): AssetsJSONSuggestWordsRepository {
        return AssetsJSONSuggestWordsRepository(context)
    }
}
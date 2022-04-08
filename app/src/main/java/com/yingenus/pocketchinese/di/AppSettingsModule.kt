package com.yingenus.pocketchinese.di

import android.content.Context
import com.yingenus.pocketchinese.ISettings
import com.yingenus.pocketchinese.Settings
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppSettingsModule {

    @Provides
    @Singleton
    fun provideSettings(context: Context): ISettings{
        return Settings.getSettings(context)
    }
}
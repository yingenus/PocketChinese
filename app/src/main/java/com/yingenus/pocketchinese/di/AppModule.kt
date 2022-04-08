package com.yingenus.pocketchinese.di

import dagger.Module

@Module(includes = [RepositoryModule::class, UseCaseModule::class, AppSettingsModule::class])
class AppModule {
}
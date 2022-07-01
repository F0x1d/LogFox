package com.f0x1d.logfox.di

import com.f0x1d.logfox.network.service.FoxBinApiService
import com.f0x1d.logfox.repository.FoxBinRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    @Singleton
    fun provideFoxBinApiService() = Retrofit.Builder()
        .baseUrl(FoxBinRepository.FOXBIN_DOMAIN)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(FoxBinApiService::class.java)
}
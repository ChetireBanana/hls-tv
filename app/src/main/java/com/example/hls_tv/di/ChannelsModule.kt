package com.example.hls_tv.di


import com.example.hls_tv.data.repository.ChannelsRepository
import com.example.hls_tv.data.repository.ChannelsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ChannelsModule {

    @Provides
    fun provideChannelsRepository(): ChannelsRepository {
        return ChannelsRepositoryImpl()
    }

}
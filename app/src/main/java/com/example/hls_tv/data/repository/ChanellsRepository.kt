package com.example.hls_tv.data.repository

interface ChannelsRepository {
    suspend fun getChannels(): String
}
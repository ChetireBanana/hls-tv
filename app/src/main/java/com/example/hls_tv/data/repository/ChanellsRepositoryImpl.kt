package com.example.hls_tv.data.repository

import com.example.hls_tv.core.config.PlaylistConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject

class ChannelsRepositoryImpl @Inject constructor(

) : ChannelsRepository {

    private val client = OkHttpClient()

    override suspend fun getChannels(): String = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(PlaylistConfig.PLAYLIST_URL)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                }
                response.body.string()
            }
        } catch (e: Exception) {
            throw IOException("Failed to fetch channels: ${e.message}", e)
        }
    }
}
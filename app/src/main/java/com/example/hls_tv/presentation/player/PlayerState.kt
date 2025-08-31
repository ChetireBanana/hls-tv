package com.example.hls_tv.presentation.player

import com.example.hls_tv.data.model.Channel

data class PlayerState(
    val allChannels: List<Channel> = emptyList(),
    val activeChannel: Channel? = null,
    val activeDurationMs: Long = 0L,
    val isPlaying: Boolean = true,
    val currentPosition: Long = 0L,
    val playbackSpeed: Float = 1f,
    val volume: Float = 1f,
    val isMuted: Boolean = false,
    val zoom: Float = 1f,
    val isLoading: Boolean = false,
    val error: Boolean? = false
)

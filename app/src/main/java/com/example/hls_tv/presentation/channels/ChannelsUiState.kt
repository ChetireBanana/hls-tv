package com.example.hls_tv.presentation.channels

import com.example.hls_tv.data.model.Channel
import com.example.hls_tv.data.model.ChannelGroup

data class ChannelsUiState (
    val channels: List<ChannelGroup> = emptyList(),
    val selectedChannels: List<Channel> = emptyList(),
    var maxChannels: Int = 1,
    val isLoading: Boolean = false,
    val error: Boolean? = false
)


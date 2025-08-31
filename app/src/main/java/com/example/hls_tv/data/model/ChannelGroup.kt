package com.example.hls_tv.data.model

data class ChannelGroup (
    val name: String?,
    val channels: List<Channel>,
    val isExpanded: Boolean = false,
)
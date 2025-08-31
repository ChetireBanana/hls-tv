package com.example.hls_tv.navigation

sealed class Screen(val route: String) {
    object ChannelList : Screen("channel_list")
    object Player : Screen("player")
}
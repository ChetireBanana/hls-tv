package com.example.hls_tv.presentation

import android.os.Message

sealed class Events {
    object NavigateToPlayer : Events()
    object NavigateToChannels : Events()
    data class ShowToast(val message: Int) : Events()
}
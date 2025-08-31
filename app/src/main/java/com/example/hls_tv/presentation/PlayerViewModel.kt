package com.example.hls_tv.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hls_tv.R
import com.example.hls_tv.data.model.Channel
import com.example.hls_tv.domain.ParseChannelsUseCase
import com.example.hls_tv.presentation.channels.ChannelsUiState
import com.example.hls_tv.presentation.player.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val parseChannelsUseCase: ParseChannelsUseCase
) : ViewModel() {

    private val _channelsState = MutableStateFlow(ChannelsUiState())
    val channelState: StateFlow<ChannelsUiState> = _channelsState.asStateFlow()

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val _events = MutableSharedFlow<Events>()
    val events: SharedFlow<Events> = _events.asSharedFlow()

    init {
        loadChannels()
    }

    fun loadChannels() {
        viewModelScope.launch {
            _channelsState.update {
                it.copy(
                    isLoading = true,
                    error = false
                )
            }
            try {
                val groupedChannels = parseChannelsUseCase.execute()
                _channelsState.update {
                    it.copy(
                        channels = groupedChannels,
                        isLoading = false
                    )
                }
            } catch (_: Exception) {
                _channelsState.update {
                    it.copy(
                        isLoading = false,
                        error = true
                    )
                }
                _events.emit(Events.ShowToast(R.string.loading_channels_error))
            }
        }
    }

    fun toggleChannelSelection(channel: Channel) {
        _channelsState.update { current ->
            if (current.selectedChannels.contains(channel)) {
                current.copy(selectedChannels = current.selectedChannels - channel)
            } else if (current.selectedChannels.size < current.maxChannels) {
                current.copy(selectedChannels = current.selectedChannels + channel)
            } else current
        }
    }

    fun changeMaxChannels(count: Int) {
        _channelsState.update { it.copy(maxChannels = count) }
    }

    fun startPlayer() {
        _playerState.update {
            it.copy(
                isPlaying = true,
                allChannels = channelState.value.selectedChannels,
                activeChannel = channelState.value.selectedChannels.first(),
                isMuted = false,
            )
        }
        viewModelScope.launch {
            _events.emit(Events.NavigateToPlayer)
        }
    }

    fun selectChannels() {
        _playerState.update {
            it.copy(
                isPlaying = false
            )
        }
        viewModelScope.launch {
            _events.emit(Events.NavigateToChannels)
        }
    }

    fun setActiveChannel(channel: Channel) {
        _playerState.update { state ->
            state.copy(
                currentPosition = 0L,
                playbackSpeed = 1f,
                volume = 1f,
                zoom = 1f,
                isMuted = false,
                activeChannel = channel
            )
        }
    }

    fun updateActiveDuration(duration: Long) {
        _playerState.update { state ->
            state.copy(activeDurationMs = duration)
        }
    }

    fun startPlayback() {
        _playerState.update { it.copy(isPlaying = true) }
    }

    fun stopPlayback() {
        _playerState.update { it.copy(isPlaying = false) }
    }

    fun setPlaybackSpeed(speed: Float) {
        _playerState.update { it.copy(playbackSpeed = speed) }
    }

    fun setVolume(volume: Float) {
        _playerState.update { it.copy(volume = volume, isMuted = volume == 0f) }
    }

    fun toggleMute() {
        _playerState.update { it.copy(isMuted = !it.isMuted) }
    }

    fun setZoom(zoom: Float) {
        _playerState.update { it.copy(zoom = zoom) }
    }

    fun setCurrentPosition(position: Long) {
        _playerState.update { it.copy(currentPosition = position) }
    }
}
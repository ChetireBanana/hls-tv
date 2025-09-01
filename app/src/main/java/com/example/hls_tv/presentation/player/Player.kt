package com.example.hls_tv.presentation.player

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import com.example.hls_tv.R
import com.example.hls_tv.data.model.Channel
import com.example.hls_tv.navigation.Screen
import com.example.hls_tv.presentation.Events
import com.example.hls_tv.presentation.PlayerViewModel
import com.example.hls_tv.presentation.elements.Error
import com.example.hls_tv.presentation.elements.Loader
import com.example.hls_tv.presentation.player.elements.PlayerChannelItem
import com.example.hls_tv.presentation.player.elements.PlayerControlPanel
import kotlinx.coroutines.delay
import kotlin.math.ceil


@Composable
fun PlayerScreenContainer(
    navController: NavController,
    viewModel: PlayerViewModel
) {
    val playerState by viewModel.playerState.collectAsState()


    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                Events.NavigateToChannels -> navController.navigate(Screen.ChannelList.route)
                Events.NavigateToPlayer -> {}
                is Events.ShowToast ->
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Поверните телефон горизогнтально")
        }
    } else {
        when {
            playerState.isLoading -> Loader()
            playerState.error == true -> {
                Error(onRetry = { viewModel.startPlayer() })
            }

            playerState.allChannels.isEmpty() -> {
                Loader()
            }

            else -> {
                Log.d("PlayerScreen", "playerState: ${playerState.allChannels.size}")
                PlayerScreen(
                    playerState = playerState,
                    onPlayPause = { if (playerState.isPlaying) viewModel.stopPlayback() else viewModel.startPlayback() },
                    onSpeedChange = { speed -> viewModel.setPlaybackSpeed(speed) },
                    onVolumeChange = { volume -> viewModel.setVolume(volume) },
                    onMuteToggle = { viewModel.toggleMute() },
                    onZoomChange = { zoom -> viewModel.setZoom(zoom) },
                    onSetCurrentPosition = { position -> viewModel.setCurrentPosition(position) },
                    onChannelSelected = { channel -> viewModel.setActiveChannel(channel) },
                    onDurationChange = { duration -> viewModel.updateActiveDuration(duration) },
                    onSelectChannelsClicked = { viewModel.selectChannels() }
                )
            }
        }
    }
}


@SuppressLint("AutoboxingStateCreation", "ConfigurationScreenWidthHeight")
@Composable
fun PlayerScreen(
    playerState: PlayerState,
    onPlayPause: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onMuteToggle: () -> Unit,
    onZoomChange: (Float) -> Unit,
    onSetCurrentPosition: (Long) -> Unit,
    onChannelSelected: (Channel) -> Unit,
    onDurationChange: (Long) -> Unit,
    onSelectChannelsClicked: () -> Unit,
    preview: Boolean = false
) {
    val context = LocalContext.current
    var controlsVisible by remember { mutableStateOf(true) }
    var currentPosition by remember { mutableStateOf(0L) }

    val exoPlayers = remember(playerState.allChannels, preview) {
        if (preview) return@remember emptyMap()

        playerState.allChannels.associateWith { channel ->
            ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(channel.url))
                playWhenReady = true
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == ExoPlayer.STATE_READY && channel == playerState.activeChannel) {
                            onDurationChange(duration)
                        }
                    }
                })
                prepare()
            }
        }
    }

    val activePlayer = exoPlayers[playerState.activeChannel]
    LaunchedEffect(
        activePlayer,
        playerState.isPlaying,
        playerState.volume,
        playerState.isMuted,
        playerState.playbackSpeed,
        playerState.currentPosition
    ) {

        activePlayer?.let { player ->
            player.playWhenReady = playerState.isPlaying
            player.setPlaybackSpeed(playerState.playbackSpeed)
            if (playerState.currentPosition > 0) {
                player.seekTo(playerState.currentPosition)
            }
        }

        exoPlayers.forEach { (channel, player) ->
            player.volume = if (channel == playerState.activeChannel && !playerState.isMuted) {
                playerState.volume
            } else {
                0f
            }
        }

        while (playerState.isPlaying){
            val position = activePlayer?.currentPosition ?: 0
            currentPosition = position
            delay(500)
        }

    }

    DisposableEffect(exoPlayers) {
        onDispose {
            exoPlayers.values.forEach { player ->
                player.release()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val totalChannels = playerState.allChannels.size
            val columns = when (totalChannels) {
                1 -> 1
                2 -> 2
                3, 4 -> 2
                else -> 3
            }
            val rows = ceil(totalChannels / columns.toFloat()).toInt()

            val itemWidth = maxWidth / columns
            val itemHeight = maxHeight / rows

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                playerState.allChannels.chunked(columns).forEach { rowChannels ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        rowChannels.forEach { channel ->
                            Box(
                                modifier = Modifier
                                    .width(itemWidth - 4.dp)
                                    .height(itemHeight - 4.dp)
                                    .clickable { onChannelSelected(channel) }
                                    .border(
                                        width = if (channel == playerState.activeChannel) 2.dp else 0.dp,
                                        color = if (channel == playerState.activeChannel) Color.Red else Color.Transparent,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            ) {
                                PlayerChannelItem(
                                    channel = channel,
                                    player = exoPlayers[channel],
                                    zoom = playerState.zoom
                                )
                            }
                        }
                    }
                }
            }
        }


        if (controlsVisible) {
            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                PlayerControlPanel(
                    playerState = playerState,
                    onPlayPause = onPlayPause,
                    onMuteToggle = onMuteToggle,
                    onVolumeChange = onVolumeChange,
                    onSpeedChange = onSpeedChange,
                    onZoomChange = onZoomChange,
                    onSetCurrentPositionSeek = onSetCurrentPosition,
                    currentPosition = currentPosition
                )
            }
        }

        SmallFloatingActionButton(
            onClick = { controlsVisible = !controlsVisible },
            modifier = Modifier
                .padding(bottom = 8.dp)
                .align(Alignment.BottomCenter)
        ) {
            Icon(
                painter = painterResource(
                    if (controlsVisible) R.drawable.outline_keyboard_arrow_down_24
                    else R.drawable.outline_keyboard_arrow_up_24
                ),
                contentDescription = "Toggle Controls"
            )
        }

        FloatingActionButton(
            onClick = onSelectChannelsClicked,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                painter = painterResource(R.drawable.computer_arrow_up_24),
                contentDescription = "Select Channels"
            )
        }
    }
}


@Preview(
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=480"
)
@Composable
fun PlayerScreenPreview() {
    PlayerScreen(
        playerState = PlayerState(
            activeChannel = Channel(
                id = "1",
                name = "Channel 1",
                logo = null,
                url = "",
                group = "Group 1"
            ),
            allChannels = listOf(
                Channel(
                    id = "1",
                    name = "Channel 1",
                    logo = null,
                    url = "",
                    group = "Group 1"
                ),
                Channel(
                    id = "2",
                    name = "Channel 2",
                    logo = null,
                    url = "",
                    group = "Group 2"
                )
            )
        ),
        preview = true,
        onPlayPause = {},
        onSpeedChange = {},
        onVolumeChange = {},
        onMuteToggle = {},
        onZoomChange = {},
        onSetCurrentPosition = {},
        onChannelSelected = {},
        onDurationChange = {},
        onSelectChannelsClicked = {}
    )
}




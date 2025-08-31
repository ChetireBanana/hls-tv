package com.example.hls_tv.presentation.player.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hls_tv.R
import com.example.hls_tv.data.model.Channel
import com.example.hls_tv.presentation.player.PlayerState

@Composable
fun PlayerControlPanel(
    playerState: PlayerState,
    onPlayPause: () -> Unit,
    onMuteToggle: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onSpeedChange: (Float) -> Unit,
    onZoomChange: (Float) -> Unit,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val duration = playerState.activeDurationMs.coerceAtLeast(1000L)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Slider(
                value = playerState.currentPosition.toFloat(),
                onValueChange = {
                    onSeek(it.toLong())
                },
                valueRange = 0f..duration.toFloat(),
                modifier = Modifier.weight(1f)
            )

        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {


                // Play/Pause
                Button(
                    onClick = {
                        onPlayPause()
                    },
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.size(60.dp),
                ) {
                    Icon(
                        painter = if (playerState.isPlaying) painterResource(R.drawable.outline_pause_24)
                        else painterResource(R.drawable.play_arrow_24),
                        contentDescription = stringResource(R.string.play_pause_content_description),
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Speed down
                Button(
                    onClick = {
                        onSpeedChange((playerState.playbackSpeed - 0.25f).coerceAtLeast(0.5f))
                    },
                    modifier = Modifier.size(36.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.speed_down_24),
                        contentDescription = stringResource(R.string.speed_down_content_description),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Speed up
                Button(
                    onClick = {
                        onSpeedChange((playerState.playbackSpeed + 0.25f).coerceAtMost(2f))
                    },
                    modifier = Modifier.size(36.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.speed_up24),
                        contentDescription = stringResource(R.string.speed_up_content_description),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Zoom out
                Button(
                    onClick = {
                        onZoomChange((playerState.zoom - 0.25f).coerceAtLeast(1f))
                    },
                    modifier = Modifier.size(36.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.zoom_out_24),
                        contentDescription = stringResource(R.string.zoom_out_content_description),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Zoom in
                Button(
                    onClick = {
                        onZoomChange((playerState.zoom + 0.25f).coerceAtMost(3f))
                    },
                    modifier = Modifier.size(36.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.zoom_in_24),
                        contentDescription = stringResource(R.string.zoom_in_content_description),
                        modifier = Modifier.size(24.dp)
                    )
                }

            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {

                // Mute/Unmute
                Button(
                    onClick = {
                        onMuteToggle()
                    },
                    modifier = Modifier.size(36.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        painter = if (playerState.isMuted) painterResource(R.drawable.unmute_24)
                        else painterResource(R.drawable.mute_24),
                        contentDescription = stringResource(R.string.mute_unmute_content_description),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Volume slider
                Slider(
                    value = playerState.volume,
                    onValueChange = { newVolume ->
                        onVolumeChange(newVolume)
                    },
                    valueRange = 0f..1f,
                    enabled = !playerState.isMuted,
                    modifier = Modifier.width(120.dp)
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=480"
)
@Composable
fun PlayerControlPanelPreview() {
    PlayerControlPanel(
        playerState = PlayerState(
            activeChannel = Channel(
                id = "1",
                name = "Channel 1",
                logo = null,
                url = "https://example.com/channel1.m3u8",
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
            ),
            isPlaying = true,
            volume = 0.8f,
            playbackSpeed = 1f,
            zoom = 1f,
            currentPosition = 30000L,
            activeDurationMs = 120000L
        ),
        onPlayPause = {},
        onMuteToggle = {},
        onVolumeChange = {},
        onSpeedChange = {},
        onZoomChange = {},
        onSeek = {}
    )
}
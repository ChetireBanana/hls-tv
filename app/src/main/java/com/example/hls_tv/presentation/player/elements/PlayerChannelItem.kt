package com.example.hls_tv.presentation.player.elements

import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.hls_tv.R
import com.example.hls_tv.data.model.Channel

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PlayerChannelItem(
    channel: Channel,
    player: ExoPlayer?,
    isActive: Boolean = false,
    zoom: Float
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = zoom
                scaleY = zoom
            }
            .clip(MaterialTheme.shapes.medium)
    ) {

        if (player != null) {
            AndroidView(
                factory = { context ->
                    PlayerView(context).apply {
                        this.player = player
                        useController = isActive
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.DarkGray)
            )
        }

        GlideImage(
            model = channel.logo ?: R.drawable.outline_android_24,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .padding(4.dp)
                .align(Alignment.TopEnd)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlayerChannelItemPreview() {
    PlayerChannelItem(
        channel = Channel(
            id = "1",
            name = "Channel 1",
            logo = null,
            url = "",
            group = "Group 1"
        ),
        player = null,
        zoom = 1f
    )
}
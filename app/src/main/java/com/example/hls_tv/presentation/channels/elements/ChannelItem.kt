package com.example.hls_tv.presentation.channels.elements

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.hls_tv.R
import com.example.hls_tv.data.model.Channel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ChannelItem(
    channel: Channel,
    isSelected: Boolean,
    canSelect: Boolean,
    onChannelClick: (Channel) -> Unit
) {

    val backgroundColor by animateColorAsState(
        if (isSelected) Color.LightGray
        else Color.White
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .alpha(if (canSelect || isSelected) 1f else 0.5f)
            .clickable(enabled = canSelect || isSelected) { onChannelClick(channel) },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlideImage(
                model = channel.logo ?: R.drawable.outline_android_24,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(MaterialTheme.shapes.small)
            )

            Spacer(Modifier.width(12.dp))

            Text(
                text = channel.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChannelItemPreview() {
    ChannelItem(
        channel = Channel(
            id = "1",
            name = "Channel 1",
            logo = null,
            group = "Group 1",
            url = ""
        ),
        isSelected = true,
        canSelect = true,
        onChannelClick = {}
    )
}
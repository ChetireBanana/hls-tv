package com.example.hls_tv.presentation.channels.elements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hls_tv.R
import com.example.hls_tv.data.model.Channel
import com.example.hls_tv.data.model.ChannelGroup

@Composable
fun ChannelGroupCard(
    group: ChannelGroup,
    selectedChannels: List<Channel>,
    maxChannels: Int,
    onChannelSelected: (Channel) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }) {

        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = group.name ?: stringResource(R.string.no_group),
                style = MaterialTheme.typography.titleMedium
            )

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(
                    animationSpec = tween(durationMillis = 200)
                ),
                exit = shrinkVertically(
                    animationSpec = tween(durationMillis = 200)
                )
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    group.channels.forEach { channel ->
                        ChannelItem(
                            channel = channel,
                            isSelected = selectedChannels.contains(channel),
                            canSelect = selectedChannels.size < maxChannels || selectedChannels.contains(
                                channel
                            ),
                            onChannelClick = { onChannelSelected(channel) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChannelGroupCardPreview() {
    ChannelGroupCard(
        group = ChannelGroup(
            name = "Group 1",
            channels = listOf(
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
                    group = "Group 1",
                ),
            )
        ),
        selectedChannels = listOf(
            Channel(
                id = "2",
                name = "Channel 2",
                logo = null,
                url = "",
                group = "Group 1"
            )
        ),
        maxChannels = 4,
        onChannelSelected = {},
    )
}
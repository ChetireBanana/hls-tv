package com.example.hls_tv.presentation.channels

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hls_tv.R
import com.example.hls_tv.data.model.Channel
import com.example.hls_tv.data.model.ChannelGroup
import com.example.hls_tv.navigation.Screen
import com.example.hls_tv.presentation.Events
import com.example.hls_tv.presentation.PlayerViewModel
import com.example.hls_tv.presentation.channels.elements.ChannelGroupCard
import com.example.hls_tv.presentation.channels.elements.ChannelItem
import com.example.hls_tv.presentation.elements.Error
import com.example.hls_tv.presentation.elements.Loader

@Composable
fun ChannelsScreenContainer(
    navController: NavController,
    viewModel: PlayerViewModel
) {
    val channelState by viewModel.channelState.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                Events.NavigateToChannels -> {}
                Events.NavigateToPlayer -> navController.navigate(Screen.Player.route)
                is Events.ShowToast ->
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (channelState.isLoading) {
        Loader()
    } else if (
        channelState.error == true
    ) {
        Error(
            onRetry = { viewModel.loadChannels() }
        )
    } else {
        ChannelsScreen(
            state = channelState,
            onChannelSelected = { viewModel.toggleChannelSelection(it) },
            onMaxChannelsChanged = { viewModel.changeMaxChannels(it) },
            onStartPlayer = { viewModel.startPlayer() }
        )
    }
}

@Composable
fun ChannelsScreen(
    state: ChannelsUiState,
    onChannelSelected: (Channel) -> Unit,
    onMaxChannelsChanged: (Int) -> Unit,
    onStartPlayer: () -> Unit
) {
    val canStartPlayer = state.selectedChannels.size == state.maxChannels
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    stringResource(R.string.chosen_channels),
                    style = MaterialTheme.typography.titleMedium
                )
                AnimatedVisibility(
                    visible = state.selectedChannels.isNotEmpty(),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        items(state.selectedChannels) { channel ->
                            Box(Modifier.animateItem()){
                                ChannelItem(
                                    channel = channel,
                                    isSelected = false,
                                    canSelect = true,
                                    onChannelClick = onChannelSelected
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.chose_channel),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            state.channels.forEach { group ->
                item {
                    ChannelGroupCard(
                        group = group,
                        selectedChannels = state.selectedChannels,
                        maxChannels = state.maxChannels,
                        onChannelSelected = onChannelSelected
                    )
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.End
        ) {

            SmallFloatingActionButton(
                onClick = { showDialog = true },
            ) {
                Icon(
                    painter = painterResource(
                        id = when (state.maxChannels) {
                            2 -> R.drawable.television_2
                            4 -> R.drawable.television_4
                            else -> R.drawable.television_1
                        }
                    ),
                    contentDescription = stringResource(R.string.select_channel_count_content_description)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LargeFloatingActionButton(
                onClick = { if (canStartPlayer) onStartPlayer() },
                modifier = Modifier
                    .alpha(if (canStartPlayer) 1f else 0.5f)
                    .padding(0.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.play_circle_outline_24),
                    contentDescription = stringResource(R.string.start_player_content_description),
                    modifier = Modifier.size(80.dp)
                )

            }
        }

    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(text = stringResource(R.string.chose_screen_count))
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(1, 2, 4).forEach { count ->
                        Button(
                            onClick = {
                                onMaxChannelsChanged(count)
                                showDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = when (count) {
                                        2 -> R.drawable.television_2
                                        4 -> R.drawable.television_4
                                        else -> R.drawable.television_1
                                    }
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "$count")
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChannelsScreenPreview() {
    ChannelsScreen(
        state = ChannelsUiState(
            channels = listOf(
                ChannelGroup(
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
                            group = "Group 1"
                        )
                    )
                ),
                ChannelGroup(
                    name = "Group 2",
                    channels = listOf(
                        Channel(
                            id = "3",
                            name = "Channel 3",
                            logo = null,
                            url = "",
                            group = "Group 2"
                        ),

                        Channel(
                            id = "4",
                            name = "Channel 4",
                            logo = null,
                            url = "",
                            group = "Group 2"
                        )
                    )
                )
            ),
            selectedChannels = listOf(
                Channel(
                    id = "1",
                    name = "Channel 1",
                    logo = null,
                    url = "",
                    group = "Group 1"
                )
            ),
        ),
        onChannelSelected = {},
        onMaxChannelsChanged = {},
        onStartPlayer = {},
    )
}
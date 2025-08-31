package com.example.hls_tv.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.hls_tv.presentation.PlayerViewModel
import com.example.hls_tv.presentation.channels.ChannelsScreenContainer
import com.example.hls_tv.presentation.player.PlayerScreenContainer


@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        navigation(
            startDestination = Screen.ChannelList.route,
            route = "main",
        ) {
            composable(route = Screen.ChannelList.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("main")
                }
                val viewModel: PlayerViewModel = hiltViewModel(parentEntry)

                ChannelsScreenContainer(
                    navController = navController,
                    viewModel = viewModel
                )
            }

            composable(route = Screen.Player.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("main")
                }
                val viewModel: PlayerViewModel = hiltViewModel(parentEntry)

                PlayerScreenContainer(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}
package com.mmusic.player

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.media3.common.util.UnstableApi
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mmusic.player.components.MBottomBar
import com.mmusic.player.ui.navigation.BottomDestination
import com.mmusic.player.ui.navigation.MMusicPlayerNavNavHost
import com.mmusic.player.ui.navigation.Screen
import com.mmusic.player.ui.navigation.checkIfDestinationIsBottom
import com.mmusic.player.ui.player.MiniPlayer
import com.mmusic.player.ui.player.PlayerActivity
import com.mmusic.player.ui.theme.MMusicPlayerTheme
import dagger.hilt.android.AndroidEntryPoint
import ir.kaaveh.sdpcompose.sdp

@AndroidEntryPoint
@UnstableApi
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            MMusicPlayerTheme {
                var title by remember {
                    mutableStateOf(BottomDestination.Music.title)
                }

                val backstack by navController.currentBackStackEntryAsState()

                val shouldShowTopAppBar = backstack?.checkIfDestinationIsBottom() ?: false
                if (backstack?.destination?.route == Screen.MusicScreen.route) {
                    title = BottomDestination.Music.title
                }
                Scaffold(
                    topBar = {
                        if (shouldShowTopAppBar)
                            MTopBar(title)
                    },
                    bottomBar = {
                        if (shouldShowTopAppBar) {
                            Column {
                                MiniPlayer(moveToPlayer = {
                                    startActivity(
                                        Intent(
                                            this@MainActivity,
                                            PlayerActivity::class.java
                                        )
                                    )
                                    //  navController.navigate(Screen.PlayerScreen.route)
                                })
                                MBottomBar(modifier = Modifier.height(45.sdp), title) { it ->
                                    title = it.title
                                    navController.navigate(it.route) {
                                        navController.graph.startDestinationRoute?.let {
                                            popUpTo(it) {
                                                saveState = true
                                            }
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        }
                    }) {
                    MMusicPlayerNavNavHost(
                        this,
                        modifier = Modifier.padding(it),
                        navController = navController,
                        startDestination = Screen.MusicScreen.route,
                    )
                }
            }
        }
    }
}


@Composable
fun MTopBar(text: String) {
    Row(
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth()
            .padding(16.sdp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        // Icon(imageVector = Icons.Filled.Search, contentDescription = null)
    }
}
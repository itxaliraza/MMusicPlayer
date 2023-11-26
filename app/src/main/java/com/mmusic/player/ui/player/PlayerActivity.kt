package com.mmusic.player.ui.player

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
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
import com.mmusic.player.ui.theme.MMusicPlayerTheme
import dagger.hilt.android.AndroidEntryPoint
import ir.kaaveh.sdpcompose.sdp

@AndroidEntryPoint
@UnstableApi
class PlayerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MMusicPlayerTheme {

               PlayerScreen {
                   finish()
               }
            }
        }
    }
}

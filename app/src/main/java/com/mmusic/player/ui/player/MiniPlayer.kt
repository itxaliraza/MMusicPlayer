package com.mmusic.player.ui.player

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.mmusic.player.R
import com.mmusic.player.components.RoundedMusicIcon
import com.mmusic.player.components.SmallMarqueeText
import ir.kaaveh.sdpcompose.sdp

@Composable
@UnstableApi
fun MiniPlayer(
    moveToPlayer: () -> Unit,
    playerViewModel: PlayerViewModel = hiltViewModel()
) {

    val musicState by playerViewModel.currentMusicState.collectAsState()
    val currentSong by playerViewModel.currentSongPlaying.collectAsState()
    val currentPosition by playerViewModel.currentPosition.collectAsState()
    LaunchedEffect(key1 = musicState.mediaId) {
        playerViewModel.updateCurrentSongPlaying(musicState.mediaId)
    }


    val progress by animateFloatAsState(
        targetValue = convertToProgress(count = currentPosition, total = musicState.duration),
        label = "ProgressAnimation"
    )

     currentSong?.let { song ->
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF0F0F0)
            ),
            elevation = CardDefaults.cardElevation(10.sdp),
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .clickable {
                        moveToPlayer()
                    }
                    .fillMaxWidth()
                    .padding(horizontal = 20.sdp, vertical = 10.sdp),
            ) {
                RoundedMusicIcon(modifier = Modifier.size(38.sdp), song = song)
                Spacer(modifier = Modifier.width(8.sdp))
                Column(horizontalAlignment = Alignment.End) {
                    SmallMarqueeText(modifier = Modifier.fillMaxWidth(), text = song.title)
                    Row {
                        MiniPlayerIconButton(drawableId = R.drawable.baseline_skip_previous_24) {
                            playerViewModel.prevClicked()
                        }
                        val drawableId =
                            if (musicState.playWhenReady) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24
                        MiniPlayerIconButton(drawableId = drawableId) {
                            if (musicState.playWhenReady) {
                                playerViewModel.pauseClicked()
                            } else
                                playerViewModel.playClicked()
                        }
                        MiniPlayerIconButton(drawableId = R.drawable.baseline_skip_next_24) {
                            playerViewModel.nextClicked()
                        }
                        MiniPlayerIconButton(drawableId = R.drawable.baseline_close_24) {
                            playerViewModel.closeClicked()
                        }


                    }
                }
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@Composable
fun MiniPlayerIconButton(drawableId: Int, btnClicked: () -> Unit) {
    IconButton(onClick = { btnClicked() }) {
        Icon(painter = painterResource(id = drawableId), contentDescription = null)
    }
}
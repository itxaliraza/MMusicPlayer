package com.mmusic.player.ui.player

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import com.mmusic.player.R
import com.mmusic.player.components.TopBarWithBack
import com.mmusic.player.domain.model.Song
import com.mmusic.player.music.MusicState
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@UnstableApi
@Composable
fun PlayerScreen(
    playerViewModel: PlayerViewModel = hiltViewModel(),
    backClicked: () -> Unit
) {

    val musicState by playerViewModel.currentMusicState.collectAsState()
    val currentPlayingQueue by playerViewModel.currentPlayingQueue.collectAsState()
    var showBottomSheet by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = musicState.mediaId) {
        playerViewModel.updateCurrentSongPlaying(musicState.mediaId)
    }

    val currentSongPlaying by playerViewModel.currentSongPlaying.collectAsState()
    val mCurrentPos by playerViewModel.currentPosition.collectAsState()
    val progress by animateFloatAsState(
        targetValue = convertToProgress(count = mCurrentPos, total = musicState.duration),
        label = "ProgressAnimation"
    )

    currentSongPlaying?.let { currentSong ->
        Column {
            TopBarWithBack(
                text = currentSong.title,
                isMarquee = true
            ) {
                backClicked()
            }

            Spacer(modifier = Modifier.height(20.sdp))

            Column(modifier = Modifier.fillMaxSize()) {
                PlayerImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.7f),
                    song = currentSong
                )

                Log.d("cvvrr", "progress == $progress")
                Slider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.sdp),
                    value = progress,
                    onValueChange = {
                        playerViewModel.seekTo(it)
                    }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.sdp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(start = 4.sdp),
                        text = mCurrentPos.toTimeString(),
                        fontSize = 13.ssp
                    )
                    Text(text = musicState.duration.toTimeString(), fontSize = 13.ssp)

                }

                Spacer(modifier = Modifier.height(30.sdp))

                BottomButtons(
                    musicState = musicState,
                    playClicked = {
                        playerViewModel.playClicked()
                    }, pauseClicked = {
                        playerViewModel.pauseClicked()

                    }, prevClicked = {
                        playerViewModel.prevClicked()

                    }, nextClicked = {
                        playerViewModel.nextClicked()
                    }, repeatClicked = {
                        playerViewModel.repeatClicked()
                    }, playlistClicked = {
                        showBottomSheet = true
                    }
                )
            }

        }
    }

    if (showBottomSheet) {
        SongsBottomSheet(
            musicState = musicState, currentPlayingQueue = currentPlayingQueue,
            dismiss = {
                showBottomSheet = false
            }, itemRemoveClicked = {
                playerViewModel.itemRemoved(it)
            },
            itemSongClicked = {
                playerViewModel.playSongs(it)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongsBottomSheet(
    musicState: MusicState,
    currentPlayingQueue: List<Song>,
    itemRemoveClicked: (Song) -> Unit,
    itemSongClicked: (Song) -> Unit,
    dismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    ModalBottomSheet(onDismissRequest = { dismiss() }, sheetState = sheetState) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.sdp, end = 8.sdp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
             Text(text = "Current Queue (${currentPlayingQueue.size})", fontWeight = FontWeight.Bold, fontSize = 14.ssp)
            IconButton(onClick = {
                dismiss()
            }) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = null)
            }
        }
        Spacer(modifier = Modifier.height(5.sdp))

        LazyColumn {
            itemsIndexed(currentPlayingQueue) { index, song ->
                SongSheetItem(
                    index = index,
                    song = song,
                    selected = musicState.mediaId == song.mediaId,
                    itemRemoveClicked = itemRemoveClicked,
                    songClicked = { itemSongClicked(song) }
                )
            }
        }
    }


}

@Composable
fun BottomButtons(
    musicState: MusicState,
    playClicked: () -> Unit,
    pauseClicked: () -> Unit,
    prevClicked: () -> Unit,
    nextClicked: () -> Unit,
    repeatClicked: () -> Unit,
    playlistClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LaunchedEffect(key1 = musicState.playbackMode) {

            Log.d("cvvrrr", "repeatMode == ${musicState.playbackMode}")
        }
        BottomButton(drawableId = musicState.playbackMode.getDrawable()) {
            repeatClicked()
        }


        BottomButton(drawableId = R.drawable.baseline_skip_previous_24) {
            prevClicked()
        }


        val drawable =
            if (musicState.playWhenReady) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24


        BottomButton(drawableId = drawable) {
            if (musicState.playWhenReady) {
                pauseClicked()
            } else
                playClicked()
        }
        BottomButton(drawableId = R.drawable.baseline_skip_next_24) {
            nextClicked()
        }


        BottomButton(drawableId = R.drawable.baseline_playlist_play_24) {
            playlistClicked()
        }


    }
}

@Composable
fun BottomButton(drawableId: Int? = null, vectorIcon: ImageVector? = null, btnClicked: () -> Unit) {
    if (vectorIcon == null) {
        IconButton(onClick = {
            btnClicked()
        }) {

            if (drawableId != null) {
                Icon(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.sdp),
                    painter = painterResource(id = drawableId),
                    contentDescription = null
                )
            }
        }
    } else {
        Image(
            modifier = Modifier
                .size(40.sdp)
                .clip(RoundedCornerShape(10.sdp))
                .clickable {
                    btnClicked()
                }
                .background(MaterialTheme.colorScheme.primary)
                .padding(6.sdp)
                .clip(RoundedCornerShape(20.sdp)),
            imageVector = vectorIcon,
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.White)
        )
    }


}


@Composable
fun PlayerImage(modifier: Modifier = Modifier, song: Song) {
    var isLoading by remember {
        mutableStateOf(false)
    }

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(song.artworkUri)
            .error(R.drawable.baseline_music_note_24)
            .placeholder(R.drawable.baseline_music_note_24)
            .scale(Scale.FILL)
            .crossfade(true)
            .build()
    )

    isLoading =
        painter.state is AsyncImagePainter.State.Loading || painter.state is AsyncImagePainter.State.Error
    Box(modifier = modifier, contentAlignment = Alignment.Center) {

        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.76f)
                .padding(30.sdp)
                .clip(RoundedCornerShape(14.sdp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(if (isLoading) 30.sdp else 0.dp),
            painter = painter,
            contentDescription = song.title,
            colorFilter = if (!isLoading) null
            else
                ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
    }
}
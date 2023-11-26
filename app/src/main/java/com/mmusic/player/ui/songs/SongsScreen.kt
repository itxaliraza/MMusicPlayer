package com.mmusic.player.ui.songs

import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.util.UnstableApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.mmusic.player.MainActivity
import com.mmusic.player.R
import com.mmusic.player.components.CircularLoading
import com.mmusic.player.components.PermissionDialog
import com.mmusic.player.components.SortingIconButton
import com.mmusic.player.data.getReadAudioPermission
import ir.kaaveh.sdpcompose.sdp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@UnstableApi
@Composable
fun SongsScreen(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    songViewModel: SongViewModel = hiltViewModel(),
    moveToPlayer: () -> Unit
) {

    val context = LocalContext.current

    val storagePermissionState = rememberPermissionState(permission = getReadAudioPermission)


    var storageDialogDeniedShow by remember {
        mutableStateOf(false)
    }
    var notificationDialogDeniedShow by remember {
        mutableStateOf(false)
    }
    var showSheet by remember {
        mutableStateOf(false)
    }


    val storagePermisissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            if (it) {
                songViewModel.fetchSongs()
            } else {
                if (!storagePermissionState.status.shouldShowRationale) {
                    Log.d("cvvrr", "Permanently denied")
                    storageDialogDeniedShow = true
                }
            }
        }
    )
    val notificationPermisissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            if (!it) {
                if (!shouldShowRequestPermissionRationale(
                        context as MainActivity, getReadAudioPermission
                    )
                ) {
                    notificationDialogDeniedShow = true
                }
            }
        }
    )
    val songState by songViewModel.songsState.collectAsState()


    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        getReadAudioPermission
                    ) == PERMISSION_GRANTED && !songViewModel.isFetching && songState.songs.isEmpty()
                ) {
                    Log.d("cvvrrr", "fetching called")
                    songViewModel.fetchSongs()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        return@DisposableEffect onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    })



    if (showSheet) {
        SortingBottomSheet(songState.sortModel, sortingApplied = {
            songViewModel.onSortClick(it)
        }, onDismiss = {
            showSheet = false
        })
    }

    Log.d("cvvr", "Recomposition SongsScreen called $showSheet")


    if (!storagePermissionState.status.isGranted) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Please allow Storage Permission to view Songs List.",
                modifier = Modifier.padding(horizontal = 20.sdp)
            )
            Spacer(modifier = Modifier.height(8.sdp))
            Button(onClick = {
                storagePermisissionLauncher.launch(getReadAudioPermission)
            }) {
                Text(text = "Request Permission")
            }


        }
    }

    if (storageDialogDeniedShow) {
        PermissionDialog(text = "You have denied storage permission . Please allow from settings") {
            storageDialogDeniedShow = false
        }
    }

    if (notificationDialogDeniedShow) {
        PermissionDialog(text = "You have denied notification permission . Please allow from settings") {
            notificationDialogDeniedShow = false
        }
    }



    DisplaySongs(
        songsUiState = songState, sortClicked = {
            showSheet = true
        }, shuffleClicked = {
            songViewModel.shuffleSongs(songState.songs)
        },
        songClicked = {
            Log.d("cvvrr", "song clicked")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) != PERMISSION_GRANTED
                ) {
                    notificationPermisissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                } else
                    songViewModel.playSongs(index = it, songState.songs) {
                        moveToPlayer()
                    }
            } else
                songViewModel.playSongs(index = it, songState.songs) {
                    moveToPlayer()
                }

        })
}


@Composable
fun DisplaySongs(
    songsUiState: SongsState,
    songClicked: (Int) -> Unit,
    sortClicked: () -> Unit,
    shuffleClicked: () -> Unit
) {
    Log.d("cvvr", "Recomposition songs called")

    val msortClicked by rememberUpdatedState(newValue = sortClicked)
    val mshuffleClicked by rememberUpdatedState(newValue = shuffleClicked)
    if (songsUiState.isLoading) {
        CircularLoading(Modifier.fillMaxSize())
    } else {
        Log.d("cvvrr", "Successs")

        val mSongs = songsUiState.songs

        Column {
            SongsTopBar(mSongs.size, msortClicked, mshuffleClicked)
            LazyColumn {
                Log.d("cvvr", "Recomposition songs list called")

                itemsIndexed(mSongs) { index, song ->
                    SongItem(song) {
                        songClicked(index)
                    }
                }
            }
        }

    }

}

@Composable
fun SongsTopBar(count: Int, sortClicked: () -> Unit, shuffleClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.sdp, vertical = 4.sdp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "$count Songs")
            Spacer(modifier = Modifier.width(8.sdp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.sdp))
                    .clickable {
                        shuffleClicked()
                    }
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 8.sdp, vertical = 4.sdp)

            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_shuffle_24),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(4.sdp))

                Text(text = "Shuffle")
            }
        }


        SortingIconButton {
            sortClicked()
        }
    }
}


@Composable
fun SongDescription(title: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = title,
        color = Color.Black.copy(alpha = 0.6f),
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )

}
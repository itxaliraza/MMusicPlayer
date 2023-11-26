package com.mmusic.player.ui.artistsdetails

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import com.mmusic.player.components.ShuffleAll
import com.mmusic.player.components.SortingIconButton
import com.mmusic.player.components.TopBarWithBack
import com.mmusic.player.ui.player.MiniPlayer
import com.mmusic.player.ui.player.PlayerActivity
import com.mmusic.player.ui.songs.SongItem
import com.mmusic.player.ui.songs.SortingBottomSheet
import ir.kaaveh.sdpcompose.sdp

@UnstableApi
@Composable
fun ArtistDetailsScreen(
    id: Long,
    artistDetailViewModel: ArtistDetailsViewModel = hiltViewModel(),
    backPressed: () -> Unit,
) {

    Log.d("cvvr", "Recomposition called $id")
    val context = LocalContext.current

    LaunchedEffect(key1 = id) {
        artistDetailViewModel.fetchArtistSongs(id)
    }

    var showSortingSheet by remember {
        mutableStateOf(false)
    }


    val artistsDetailsUiState by artistDetailViewModel.artistDetailsUiState.collectAsState()

    Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {
            Log.d("cvvr", "Recomposition Column called")

            TopBarWithBack(backBtnPressed = backPressed, text = artistsDetailsUiState.artistName)

            Spacer(modifier = Modifier.height(10.sdp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShuffleAll(
                    modifier = Modifier.padding(start = 10.sdp),
                    total = artistsDetailsUiState.songsList.size
                ) {
                    artistDetailViewModel.shuffleSongs(artistsDetailsUiState.songsList)
                }

                SortingIconButton(modifier = Modifier.padding(end = 10.sdp)) {
                    showSortingSheet = true
                }

            }
            Spacer(modifier = Modifier.height(15.sdp))
            LazyColumn {
                Log.d("cvvr", "Recomposition list called")

                itemsIndexed(artistsDetailsUiState.songsList) { index, song ->
                    Log.d("cvvr", "songs list composed")
                    SongItem(song = song) {
                        artistDetailViewModel.playSongs(artistsDetailsUiState.songsList, index)
                    }
                }
            }

            if (showSortingSheet) {
                SortingBottomSheet(onDismiss = {
                    showSortingSheet = false

                }, sortingApplied = {
                    artistDetailViewModel.onSortClick(it)
                },
                    sortModel = artistsDetailsUiState.sortModel,
                    isForArtistOrAlbums = true
                )
            }
        }

        MiniPlayer(moveToPlayer = {
            context.startActivity(Intent(context, PlayerActivity::class.java))
        })
    }

}


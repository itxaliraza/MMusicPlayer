package com.mmusic.player.ui.albums

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import com.mmusic.player.R
import com.mmusic.player.components.CircularLoading
import com.mmusic.player.domain.model.AlbumModel
import com.mmusic.player.ui.songs.SongDescription
import ir.kaaveh.sdpcompose.sdp

@Composable
fun AlbumsScreen(albumViewModel: AlbumViewModel = hiltViewModel(),albumClicked:(Long)->Unit) {
    val albumsState by albumViewModel.albumsState.collectAsState()
    DisplayAlbums(albumsState, albumClicked)
}

@Composable
fun DisplayAlbums(albumsState: AlbumsUiState,albumClicked:(Long)->Unit) {
    if (albumsState.isLoading) {
        CircularLoading(Modifier.fillMaxSize())
    } else {
        val albumsList = albumsState.albums.toList()
        LazyColumn {
            items(albumsList) {
                AlbumItem(it,albumClicked=albumClicked)
            }
        }

    }
}

@Composable
fun AlbumItem(albumValue: Pair<Long, AlbumModel>,albumClicked:(Long)->Unit) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.sdp)
            .background(Color.White)
            .clickable {
                albumClicked(albumValue.first)
            }
    ) {
        var isLoading by remember {
            mutableStateOf(false)
        }

        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(albumValue.second.artWorkUri)
                .error(R.drawable.baseline_album_24)
                .placeholder(R.drawable.baseline_album_24)
                .scale(Scale.FILL)
                .crossfade(true)
                //  .size(100,100)
                .build()
        )

        isLoading =
            painter.state is AsyncImagePainter.State.Error || painter.state is AsyncImagePainter.State.Loading
        Image(
            modifier = Modifier
                .size(48.sdp)
                .clip(RoundedCornerShape(6.sdp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            painter = painter,
            contentDescription = null,
            colorFilter = if (isLoading)
                ColorFilter.tint(MaterialTheme.colorScheme.primary)
            else null
        )



        Spacer(modifier = Modifier.width(8.sdp))


        Column {
            Text(
                text = albumValue.second.album,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(8.sdp))
            Row {
                SongDescription(title = "${albumValue.second.songsList.size} Tracks")
                Spacer(modifier = Modifier.width(5.sdp))

                HorizontalDivider(
                    modifier = Modifier
                        .height(14.sdp)
                        .width(1.sdp),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(5.sdp))
                SongDescription(title = albumValue.second.artist)
            }
        }

    }
}

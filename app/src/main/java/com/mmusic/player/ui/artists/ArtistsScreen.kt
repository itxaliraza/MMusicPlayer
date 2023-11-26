package com.mmusic.player.ui.artists

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
import com.mmusic.player.domain.model.ArtistsModel
import com.mmusic.player.ui.songs.SongDescription
import ir.kaaveh.sdpcompose.sdp

@Composable
fun ArtistsScreen(artistViewModel: ArtistViewModel = hiltViewModel(), artistClicked:(Long)->Unit) {
    val artistState by artistViewModel.artistsState.collectAsState()
    DisplayArtists(artistState,artistClicked)
}

@Composable
fun DisplayArtists(artistState: ArtistsUiState,artistClicked:(Long)->Unit) {
    if (artistState.isLoading) {
        CircularLoading(Modifier.fillMaxSize())
    } else {
        val artistsList = artistState.artists.toList()
        LazyColumn {
            items(artistsList) {
                ArtistItem(it,artistClicked)
            }
        }

}
}

@Composable
fun ArtistItem(item: Pair<Long, ArtistsModel>, artistClicked:(Long)->Unit) {
    var isLoading by remember {
        mutableStateOf(false)
    }

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current).data(item.second.artworkUri)
            .error(R.drawable.music_artist)
            .placeholder(R.drawable.music_artist)
            .scale(Scale.FILL).crossfade(true).build()
    )

    isLoading =
        painter.state is AsyncImagePainter.State.Loading || painter.state is AsyncImagePainter.State.Error



    Row(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                artistClicked(item.first!!)
            }
            .padding(8.sdp)
            .background(Color.White),
    ) {
        Image(
            modifier = Modifier
                .size(48.sdp)
                .clip(RoundedCornerShape(6.sdp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(if (isLoading) 7.sdp else 0.sdp),
            painter = painter,
            contentDescription = null,
            colorFilter = if (isLoading) ColorFilter.tint(MaterialTheme.colorScheme.primary)
            else null
        )
        Spacer(modifier = Modifier.width(8.sdp))

        Column {
            Text(
                text = item.second.artistName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.sdp))
            SongDescription(title = "${item.second.songsList.size} Songs")

        }
    }
}

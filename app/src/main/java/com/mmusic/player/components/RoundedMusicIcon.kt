package com.mmusic.player.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import com.mmusic.player.R
import com.mmusic.player.domain.model.Song
import ir.kaaveh.sdpcompose.sdp

@Composable
fun RoundedMusicIcon(modifier: Modifier = Modifier, song: Song) {
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
            //  .size(100,100)
            .build()
    )
    isLoading =
        painter.state is AsyncImagePainter.State.Error || painter.state is AsyncImagePainter.State.Loading
    Image(
        modifier = modifier
            .clip(RoundedCornerShape(6.sdp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(if (isLoading) 8.sdp else 0.dp),
        painter = painter,
        contentDescription = song.title,
        colorFilter = if (!isLoading) null
        else
            ColorFilter.tint(MaterialTheme.colorScheme.primary)
    )


}
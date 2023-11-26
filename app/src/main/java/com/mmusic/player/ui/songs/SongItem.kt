package com.mmusic.player.ui.songs

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import com.mmusic.player.R
import com.mmusic.player.components.RoundedMusicIcon
import com.mmusic.player.domain.model.Song
import ir.kaaveh.sdpcompose.sdp

@Composable
fun SongItem(song: Song, songClicked: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.sdp)
            .clickable {
                songClicked?.invoke()
            }
            .background(Color.White)
    ) {


        RoundedMusicIcon(modifier = Modifier.size(48.sdp), song = song)


        Spacer(modifier = Modifier.width(8.sdp))


        Column {
            Text(
                text = song.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(8.sdp))
            Row {
                SongDescription(title = song.durationValue)
                Spacer(modifier = Modifier.width(5.sdp))

                HorizontalDivider(
                    modifier = Modifier
                        .height(14.sdp)
                        .width(1.sdp),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(5.sdp))
                SongDescription(title = song.artist)
            }
        }

    }
}

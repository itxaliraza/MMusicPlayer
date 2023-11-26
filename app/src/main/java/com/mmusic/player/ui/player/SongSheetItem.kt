package com.mmusic.player.ui.player

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
 import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.mmusic.player.domain.model.Song
import com.mmusic.player.ui.songs.SongDescription
import ir.kaaveh.sdpcompose.sdp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongSheetItem(index: Int,selected:Boolean, song: Song,itemRemoveClicked:(Song)->Unit, songClicked: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.sdp)
            .clickable {
                songClicked?.invoke()
            }
     ) {

        Spacer(modifier = Modifier.width(6.sdp))

        Text(
            text = "${index + 1}. ",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.sdp),
            fontWeight = FontWeight.Bold
        )


        Spacer(modifier = Modifier.width(9.sdp))


        Column(modifier = Modifier.fillMaxWidth(0.85f)) {
            Text(
                text = song.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
                color = if (selected) MaterialTheme.colorScheme.primary else Color.Black,
                fontWeight = if (selected) FontWeight.Bold else null,
                modifier = if (selected) Modifier.basicMarquee() else Modifier
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

        IconButton(modifier = Modifier.align(Alignment.Top), onClick = {
            itemRemoveClicked(song)
        }) {
            Icon(imageVector = Icons.Default.Close, contentDescription =null)
        }

    }
}

package com.mmusic.player.ui.folders

import androidx.compose.foundation.Image
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
import com.mmusic.player.domain.model.FolderModel
import com.mmusic.player.ui.songs.SongDescription
import ir.kaaveh.sdpcompose.sdp

@Composable
fun FoldersScreen(folderViewModel: FolderViewModel = hiltViewModel(),folderClicked:(String)->Unit) {
    val folders by folderViewModel.foldersState.collectAsState()
    DisplayFolders(folders,folderClicked)
}

@Composable
fun DisplayFolders(foldersState: FolderUiState, folderClicked: (String) -> Unit) {
    if (foldersState.isLoading) {
        CircularLoading(Modifier.fillMaxSize())
    } else {
        val foldersList = foldersState.folders.toList()
        LazyColumn {
            items(foldersList) {
                FolderItem(it,folderClicked)
            }
        }
    }
}

@Composable
fun FolderItem(item: Pair<String, FolderModel>, folderClicked: (String) -> Unit) {
    var isLoading by remember {
        mutableStateOf(false)
    }

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            //.data(item.second.artworkUri)
            //      .error(R.drawable.music_artist)
            .placeholder(R.drawable.baseline_folder_24)
            .error(R.drawable.baseline_folder_24)
            .scale(Scale.FILL).crossfade(true).build()
    )

    isLoading =
        painter.state is AsyncImagePainter.State.Loading || painter.state is AsyncImagePainter.State.Error



    Row(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                folderClicked(item.first)
            }
            .padding(8.sdp)
            .background(Color.White),
    ) {
        Image(
            modifier = Modifier
                .size(48.sdp)
                .clip(RoundedCornerShape(6.sdp))
                .padding(if (isLoading) 7.sdp else 0.sdp),
            painter = painter,
            contentDescription = null,
            colorFilter = if (isLoading) ColorFilter.tint(MaterialTheme.colorScheme.primary)
            else null
        )
        Spacer(modifier = Modifier.width(8.sdp))

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    text = item.second.folderName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge
                )
                SongDescription(
                    modifier = Modifier.padding(horizontal = 8.sdp),
                    title = "${item.second.songsList.size} Songs"
                )

            }
            Spacer(modifier = Modifier.height(8.sdp))
            SongDescription(title = item.second.folderPath)

        }
    }
}

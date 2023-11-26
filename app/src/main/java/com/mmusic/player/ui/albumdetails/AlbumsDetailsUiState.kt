package com.mmusic.player.ui.albumdetails

import com.mmusic.player.domain.model.Song
import com.mmusic.player.domain.model.SortModel


data class AlbumsDetailsUiState(
    val albumName:String="",
    val songsList: List<Song> = listOf(),
    val sortModel: SortModel=SortModel()
)
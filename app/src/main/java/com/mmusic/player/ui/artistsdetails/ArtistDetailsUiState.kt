package com.mmusic.player.ui.artistsdetails

import com.mmusic.player.domain.model.Song
import com.mmusic.player.domain.model.SortModel


data class ArtistDetailsUiState(
    val artistName:String="",
    val songsList: List<Song> = listOf(),
    val sortModel: SortModel=SortModel()
)
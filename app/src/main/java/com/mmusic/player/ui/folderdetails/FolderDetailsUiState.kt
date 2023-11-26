package com.mmusic.player.ui.folderdetails

import com.mmusic.player.domain.model.Song
import com.mmusic.player.domain.model.SortModel


data class FolderDetailsUiState(
    val artistName:String="",
    val songsList: List<Song> = listOf(),
    val sortModel: SortModel=SortModel()
)
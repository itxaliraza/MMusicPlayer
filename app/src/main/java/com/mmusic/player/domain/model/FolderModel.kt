package com.mmusic.player.domain.model

data class FolderModel(
    val folderPath: String,
    val folderName: String,
    val songsList: ArrayList<Song>
)
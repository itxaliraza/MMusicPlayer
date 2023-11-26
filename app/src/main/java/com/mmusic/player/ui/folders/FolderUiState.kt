package com.mmusic.player.ui.folders

import com.mmusic.player.domain.model.AlbumModel
import com.mmusic.player.domain.model.ArtistsModel
import com.mmusic.player.domain.model.FolderModel
import java.util.concurrent.ConcurrentHashMap



data class FolderUiState(
    var isLoading: Boolean = true,
    val folders: Map<String, FolderModel> = mapOf()
)
package com.mmusic.player.ui.albums

import com.mmusic.player.domain.model.AlbumModel


data class AlbumsUiState(
    var isLoading: Boolean = true,
    val albums: Map<Long, AlbumModel> = mapOf()
)
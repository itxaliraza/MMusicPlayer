package com.mmusic.player.ui.artists

import com.mmusic.player.domain.model.AlbumModel
import com.mmusic.player.domain.model.ArtistsModel
import java.util.concurrent.ConcurrentHashMap


data class ArtistsUiState(
    var isLoading: Boolean = true,
    val artists: Map<Long, ArtistsModel> = mapOf()
)
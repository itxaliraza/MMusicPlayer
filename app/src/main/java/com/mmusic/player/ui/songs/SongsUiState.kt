package com.mmusic.player.ui.songs

import com.mmusic.player.domain.model.Song
import com.mmusic.player.domain.model.SortModel


data class SongsState(
    val sortModel: SortModel = SortModel(),
    val songs: List<Song> = listOf(),
    val isLoading:Boolean=true,
 )
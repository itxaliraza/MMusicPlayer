package com.mmusic.player.ui.albumdetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.mmusic.player.data.MediaRepositoryImpl
import com.mmusic.player.domain.model.Song
import com.mmusic.player.domain.model.SortModel
import com.mmusic.player.music.MusicServiceConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@UnstableApi
@HiltViewModel
class AlbumDetailsViewModel @Inject constructor(
    private val mediaRepository: MediaRepositoryImpl,
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {


    private val _albumsDetailsUiState: MutableStateFlow<AlbumsDetailsUiState> =
        MutableStateFlow(AlbumsDetailsUiState())
    val albumsDetailsUiState = _albumsDetailsUiState.asStateFlow()


    fun getAlbumSongs(id: Long) {

        _albumsDetailsUiState.update {
            it.copy(songsList = mediaRepository.albums.value[id]?.songsList ?: listOf())
        }
    }

    fun fetchAlbumSongs(id: Long) {
        _albumsDetailsUiState.update {
            it.copy(albumName = mediaRepository.albums.value[id]?.album ?: "Unknown")
        }
        getAlbumSongs(id)
    }

    fun playSongs(song: List<Song>,startingIndex:Int) {
        musicServiceConnection.playSongs(songs = song, startIndex = startingIndex)
    }

    fun shuffleSongs(song: List<Song>) {
        musicServiceConnection.shuffleSongs(songs = song)
    }

    fun onSortClick(sortModel: SortModel) {
        viewModelScope.launch {
            Log.d("cvvr", "Sort clicked $sortModel")

            val songsList = _albumsDetailsUiState.value.songsList

            mediaRepository.sortSongs(songsList, sortModel) { sortedSongs ->
                _albumsDetailsUiState.update {
                    it.copy(songsList = sortedSongs, sortModel = sortModel)
                }
            }

        }
    }


}
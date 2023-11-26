package com.mmusic.player.ui.artistsdetails

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
class ArtistDetailsViewModel @Inject constructor(
    private val mediaRepository: MediaRepositoryImpl,
    private val musicServiceConnection: MusicServiceConnection
) :
    ViewModel() {


    private val _artistDetailsUiState: MutableStateFlow<ArtistDetailsUiState> =
        MutableStateFlow(ArtistDetailsUiState())
    val artistDetailsUiState = _artistDetailsUiState.asStateFlow()


    fun fetchArtistSongs(id: Long) {
        _artistDetailsUiState.update {
            it.copy(artistName = mediaRepository.artists.value[id]?.artistName ?: "Unknown")
        }
        getArtistSongs(id)

    }

    fun getArtistSongs(id: Long) {

        _artistDetailsUiState.update {
            it.copy(songsList = mediaRepository.artists.value[id]?.songsList ?: listOf())
        }
    }

    fun onSortClick(sortModel: SortModel) {
        viewModelScope.launch {
            Log.d("cvvr", "Sort clicked $sortModel")

            val songsList = _artistDetailsUiState.value.songsList

            mediaRepository.sortSongs(songsList, sortModel) { sortedSongs ->
                _artistDetailsUiState.update {
                    it.copy(songsList = sortedSongs, sortModel = sortModel)
                }
            }

        }
    }

    fun shuffleSongs(songsList: List<Song>) {
        musicServiceConnection.shuffleSongs(songsList)
    }

    fun playSongs(songsList: List<Song>, startIndex: Int) {
        musicServiceConnection.playSongs(songsList, startIndex)
    }


}
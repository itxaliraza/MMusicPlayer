package com.mmusic.player.ui.artists

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmusic.player.data.MediaRepositoryImpl
import com.mmusic.player.domain.model.SortModel
import com.mmusic.player.ui.albumdetails.AlbumsDetailsUiState
import com.mmusic.player.ui.artistsdetails.ArtistDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(private val mediaRepository: MediaRepositoryImpl) :
    ViewModel() {



    private val _artistsState: MutableStateFlow<ArtistsUiState> =
        MutableStateFlow(ArtistsUiState())
    val artistsState = _artistsState.asStateFlow()

    private val _artistDetailsUiState: MutableStateFlow<ArtistDetailsUiState> =
        MutableStateFlow(ArtistDetailsUiState())
    val artistDetailsUiState = _artistDetailsUiState.asStateFlow()


    init {
        mediaRepository.artists.onEach { mapp ->
            _artistsState.update {
                it.copy(isLoading = false, artists = mapp)
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, artistsState)
    }


    fun fetchArtistSongs(id: Long) {
        _artistDetailsUiState.update {
            it.copy(artistName = artistsState.value.artists[id]?.artistName ?: "Unknown")
        }
        getArtistSongs(id)

    }

    fun getArtistSongs(id: Long) {

        _artistDetailsUiState.update {
            it.copy(songsList =artistsState.value.artists[id]?.songsList?: listOf() )
        }
    }

    fun onSortClick(sortModel: SortModel) {
        viewModelScope.launch {
            Log.d("cvvr", "Sort clicked $sortModel")

            val songsList = _artistDetailsUiState.value.songsList

            mediaRepository.sortSongs(songsList, sortModel) {sortedSongs->
                _artistDetailsUiState.update {
                    it.copy(songsList =sortedSongs, sortModel = sortModel)
                }
            }

        }
    }

}
package com.mmusic.player.ui.albums

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmusic.player.data.MediaRepositoryImpl
import com.mmusic.player.domain.model.SortModel
import com.mmusic.player.ui.albumdetails.AlbumsDetailsUiState
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
class AlbumViewModel @Inject constructor(
    private val mediaRepository: MediaRepositoryImpl,
) : ViewModel() {

    private val _albumsState: MutableStateFlow<AlbumsUiState> =
        MutableStateFlow(AlbumsUiState())
    val albumsState = _albumsState.asStateFlow()


    private val _albumsDetailsUiState: MutableStateFlow<AlbumsDetailsUiState> =
        MutableStateFlow(AlbumsDetailsUiState())
    val albumsDetailsUiState = _albumsDetailsUiState.asStateFlow()


//    private val _albumDetailsSongsList: MutableStateFlow<List<Song>> = MutableStateFlow(listOf())
//
//    val albumDetailsSongsList = _albumDetailsSongsList.asStateFlow()

    init {
        Log.d("cvvrr","Albumviewmodel init")
        mediaRepository.albums.onEach { mapp ->
            _albumsState.update {
                it.copy(isLoading = false, albums = mapp)
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, albumsState)
    }


    fun getAlbumSongs(id: Long) {

        _albumsDetailsUiState.update {
            it.copy(songsList =albumsState.value.albums[id]?.songsList?: listOf() )
        }
    }

    fun fetchAlbumSongs(id: Long) {
        _albumsDetailsUiState.update {
            it.copy(albumName = albumsState.value.albums[id]?.album ?: "Unknown")
        }
        getAlbumSongs(id)

     }


    fun onSortClick(sortModel: SortModel) {
        viewModelScope.launch {
            Log.d("cvvr", "Sort clicked $sortModel")

            val songsList = _albumsDetailsUiState.value.songsList

            mediaRepository.sortSongs(songsList, sortModel) {sortedSongs->
                _albumsDetailsUiState.update {
                    it.copy(songsList =sortedSongs, sortModel = sortModel)
                }
             }

        }
    }


}
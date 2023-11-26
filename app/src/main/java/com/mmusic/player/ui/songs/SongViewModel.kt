package com.mmusic.player.ui.songs

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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@UnstableApi
@HiltViewModel
class SongViewModel @Inject constructor(
    private val mediaRepository: MediaRepositoryImpl,
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    val isFetching: Boolean = mediaRepository.isFetching

    private val _songsState: MutableStateFlow<SongsState> =
        MutableStateFlow(SongsState())
    val songsState = _songsState.asStateFlow()


    init {
        Log.d("cvvr", "SongViewModel init")
        fetchSongs()
    }

    fun onSortClick(sortModel: SortModel) {
        viewModelScope.launch {
            Log.d("cvvr", "Sort clicked $sortModel")

            val songsList = _songsState.value.songs
            mediaRepository.sortSongs(songsList, sortModel) { sortedSongs ->

                _songsState.update {
                    it.copy(songs = sortedSongs, sortModel = sortModel)
                }
            }

        }
    }


    fun shuffleSongs(songs: List<Song>) {
        viewModelScope.launch {
            musicServiceConnection.shuffleSongs(songs = songs)
        }
    }

    fun playSongs(index: Int, songs: List<Song>, moveToPlayer: () -> Unit) {
        viewModelScope.launch {
            Log.d("cvvrr", "before playSongs")

            if (musicServiceConnection.musicState.value.mediaId == songs[index].mediaId) {
                Log.d("cvvrr", "after playSongs")

                moveToPlayer()
            } else {
                musicServiceConnection.playSongs(songs = songs, startIndex = index)
                Log.d("cvvrr", "after playSongs 2")

            }
        }
    }

    fun fetchSongs() {
        viewModelScope.launch {
            mediaRepository.getSongs().onEach { songsList ->
                _songsState.update {
                    it.copy(songs = songsList, isLoading = false)
                }
            }.stateIn(viewModelScope, SharingStarted.Eagerly, arrayListOf())
        }
    }


}
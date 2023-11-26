package com.mmusic.player.ui.folderdetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

@HiltViewModel
class FolderDetailsViewModel @Inject constructor(
    private val mediaRepository: MediaRepositoryImpl,
    private val musicServiceConnection: MusicServiceConnection
) :
    ViewModel() {


    private val folderDetailsUiState: MutableStateFlow<FolderDetailsUiState> =
        MutableStateFlow(FolderDetailsUiState())
    val folderDetailsUiStateState = folderDetailsUiState.asStateFlow()


    fun fetchFolderSongs(path: String) {
        folderDetailsUiState.update {
            it.copy(artistName = mediaRepository.folders.value[path]?.folderName ?: "Unknown")
        }
        getFolderSongs(path)

    }

    private fun getFolderSongs(path: String) {

        folderDetailsUiState.update {
            it.copy(songsList = mediaRepository.folders.value[path]?.songsList ?: listOf())
        }
    }

    fun onSortClick(sortModel: SortModel) {
        viewModelScope.launch {
            Log.d("cvvr", "Sort clicked $sortModel")

            val songsList = folderDetailsUiState.value.songsList

            mediaRepository.sortSongs(songsList, sortModel) { sortedSongs ->
                folderDetailsUiState.update {
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
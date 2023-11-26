package com.mmusic.player.ui.folders

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmusic.player.data.MediaRepositoryImpl
import com.mmusic.player.domain.model.SortModel
import com.mmusic.player.ui.folderdetails.FolderDetailsUiState
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
class FolderViewModel @Inject constructor(private val mediaRepository: MediaRepositoryImpl) :
    ViewModel() {

    private val _folderUiState: MutableStateFlow<FolderUiState> =
        MutableStateFlow(FolderUiState())
    val foldersState = _folderUiState.asStateFlow()

    private val folderDetailsUiState: MutableStateFlow<FolderDetailsUiState> =
        MutableStateFlow(FolderDetailsUiState())
    val folderDetailsUiStateState = folderDetailsUiState.asStateFlow()


    init {
        mediaRepository.folders.onEach {mapp->
             _folderUiState.update {
                it.copy(isLoading = false, folders = mapp)
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, _folderUiState)
    }


    fun fetchFolderSongs(path:String) {
        folderDetailsUiState.update {
            it.copy(artistName = foldersState.value.folders[path]?.folderName ?: "Unknown")
        }
        getFolderSongs(path)

    }

    private fun getFolderSongs(path:String) {

        folderDetailsUiState.update {
            it.copy(songsList =foldersState.value.folders[path]?.songsList?: listOf() )
        }
    }

    fun onSortClick(sortModel: SortModel) {
        viewModelScope.launch {
            Log.d("cvvr", "Sort clicked $sortModel")

            val songsList = folderDetailsUiState.value.songsList

            mediaRepository.sortSongs(songsList, sortModel) {sortedSongs->
                folderDetailsUiState.update {
                    it.copy(songsList =sortedSongs, sortModel = sortModel)
                }
            }

        }
    }
}
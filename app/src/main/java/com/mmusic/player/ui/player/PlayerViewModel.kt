package com.mmusic.player.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.mmusic.player.data.MediaRepositoryImpl
import com.mmusic.player.data.PrefHelper
import com.mmusic.player.domain.model.Song
import com.mmusic.player.domain.model.togglePlaybackMode
import com.mmusic.player.music.MusicServiceConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@UnstableApi
@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val musicRepositoryImpl: MediaRepositoryImpl,
    private val prefHelper: PrefHelper
) : ViewModel() {


    val currentPlayingQueue =
        musicRepositoryImpl.getPlayingQueueSongsFlow().stateIn(
            viewModelScope,
            SharingStarted.Eagerly, emptyList()
        )
    val currentMusicState = musicServiceConnection.musicState

    val currentPosition = musicServiceConnection.currentPosition.stateIn(
        viewModelScope,
        SharingStarted.Eagerly, 0
    )


    private val _currentSongPlaying: MutableStateFlow<Song?> = MutableStateFlow(null)
    val currentSongPlaying = _currentSongPlaying.asStateFlow()

    fun updateCurrentSongPlaying(mediaId: String) {
        Log.d("cvvr", "mediaId =$mediaId")
        _currentSongPlaying.value = musicRepositoryImpl.getCurrentSongPlayed(mediaId)
    }

    fun playClicked() {
        musicServiceConnection.play()
    }

    fun pauseClicked() {
        musicServiceConnection.pause()
    }

    fun prevClicked() {
        musicServiceConnection.seekToPrevious()
    }

    fun nextClicked() {
        musicServiceConnection.seekToNext()
    }

    fun closeClicked() {
        musicServiceConnection.stopController()
    }

    fun repeatClicked() {
        val newPlaybackMode = prefHelper.repeatMode.togglePlaybackMode()
        prefHelper.repeatMode = newPlaybackMode
        Log.d("cvvrrr", "new playbackmoded =$newPlaybackMode")
        musicServiceConnection.repeatClicked(newPlaybackMode)
    }

    fun seekTo(pos: Float) {
        musicServiceConnection.seekToPos(convertToPosition(pos, currentMusicState.value.duration))
    }

    fun itemRemoved(it: Song) {
        viewModelScope.launch(Dispatchers.IO) {
            val index = musicRepositoryImpl.getPlayingQueueSongs().indexOf(it)
            if (index == -1)
                return@launch
            Log.d("cvvrrr", "index =$index")
            musicServiceConnection.itemRemoved(index)
            musicRepositoryImpl.removeItemFromPlayingQueue(index)
        }

    }

    fun playSongs(it: Song) {
        viewModelScope.launch {
            val index = musicRepositoryImpl.getPlayingQueueSongs().indexOf(it)
            musicServiceConnection.seekToSong(index)
        }
    }
}

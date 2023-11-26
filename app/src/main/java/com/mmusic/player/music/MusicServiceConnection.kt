package com.mmusic.player.music

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.Player
import androidx.media3.common.Player.EVENT_MEDIA_ITEM_TRANSITION
import androidx.media3.common.Player.EVENT_MEDIA_METADATA_CHANGED
import androidx.media3.common.Player.EVENT_PLAYBACK_STATE_CHANGED
import androidx.media3.common.Player.EVENT_PLAYER_ERROR
import androidx.media3.common.Player.EVENT_PLAY_WHEN_READY_CHANGED
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.mmusic.player.data.MediaRepositoryImpl
import com.mmusic.player.di.MyCoroutineDispatchers
import com.mmusic.player.di.MyDispatcher
import com.mmusic.player.domain.model.PlaybackMode
import com.mmusic.player.domain.model.Song
import com.mmusic.player.domain.model.asMediaItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@UnstableApi
@Singleton
class MusicServiceConnection @Inject constructor(
    @ApplicationContext private val context: Context,
    @MyDispatcher(MyCoroutineDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    @MyDispatcher(MyCoroutineDispatchers.MAIN) mainDispatcher: CoroutineDispatcher,
    private var mediaRepository: MediaRepositoryImpl
) {
    private val mainCoroutineScope = CoroutineScope(mainDispatcher + SupervisorJob())
    var shufflingJob: Job? = null

    private val _musicState = MutableStateFlow(MusicState())
    val musicState = _musicState.asStateFlow()
    val currentPosition = flow {
        while (currentCoroutineContext().isActive) {
            val currentPosition = mediaController?.currentPosition ?: 0
            emit(currentPosition)
            kotlinx.coroutines.delay(1.milliseconds)
        }
    }

    private var mediaController: MediaController? = null

    init {
        initMediaController()

    }

    private fun initMediaController(controllerInitialized: (() -> Unit)? = null) {
        mainCoroutineScope.launch {
            mediaController = MediaController.Builder(
                context,
                SessionToken(
                    context, ComponentName(context, MyPlayerService::class.java)
                )
            ).buildAsync().await().apply {
                addListener(PlayerListener())
            }

            Log.d("cvvrr", "controllerInitialized")
            controllerInitialized?.invoke()
        }
    }

    inner class PlayerListener : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            if (events.containsAny(
                    EVENT_PLAYBACK_STATE_CHANGED,
                    EVENT_MEDIA_METADATA_CHANGED,
                    EVENT_PLAY_WHEN_READY_CHANGED,
                )
            ) {
                updatePlayerState(player)
            }
            if (events.contains(EVENT_PLAYER_ERROR)) {
                Log.d("cvvrrrr", "EVENT_PLAYER_ERROR")
                seekToNext()

            }

            if (events.contains(EVENT_MEDIA_ITEM_TRANSITION)) {
                val index = player.currentMediaItemIndex
                _musicState.update {
                    it.copy(currentSongIndex = index)
                }
            }
        }
    }

    private fun updatePlayerState(player: Player) = with(player) {

        _musicState.update {
            it.copy(
                mediaId = currentMediaItem?.mediaId ?: "",
                playbackState = playbackState.asPlaybackState(),
                duration = duration,
                playWhenReady = playWhenReady
            )
        }
    }

    fun playSongs(songs: List<Song>, startIndex: Int = 0, startPosition: Long = 0L) {
        mainCoroutineScope.launch {
            if (mediaController?.isConnected == false) {
                initMediaController {
                    startMediaPlayback(songs, startIndex, startPosition)
                }
                android.util.Log.d("cvvrr", "mediacontroller not connected")
            } else {
                startMediaPlayback(songs, startIndex, startPosition)
            }
                mediaRepository.setPlayingQueueIds(songs.map { it.mediaId })
                android.util.Log.d("cvvrr", "setPlayingQueueIds")

        }
    }

    fun shuffleSongs(songs: List<Song>, startPosition: Long = 0, startIndex: Int = 0) {
        try {
            shufflingJob?.cancel()
        } catch (e: Exception) {
            Log.d("cvvrrr", e.message.toString())
        }
        shufflingJob = mainCoroutineScope.launch {
            var shufflesSongs: List<Song>
            withContext(Dispatchers.IO) {
                shufflesSongs = songs.shuffled()
            }
            if (mediaController?.isConnected == false) {
                initMediaController {
                    startMediaPlayback(shufflesSongs, startIndex, startPosition)
                }
            } else {
                startMediaPlayback(shufflesSongs, startIndex, startPosition)
            }
            mediaRepository.setPlayingQueueIds(shufflesSongs.map { it.mediaId })
        }
    }

    fun seekToPos(pos: Long) = mediaController?.run {
        seekTo(pos)
        play()
    }

    fun seekToSong(itemIndex: Int) = mediaController?.run {
        seekTo(itemIndex,0)
        play()
    }


    fun seekToPrevious() = mediaController?.run {
        seekToPrevious()
        play()
    }

    fun seekToNext() = mediaController?.run {
        seekToNext()
        play()
    }

    fun play() = mediaController?.play()

    fun pause() = mediaController?.pause()

    fun repeatClicked(repeatModee: PlaybackMode) {
        mediaController?.run {
            when (repeatModee) {
                PlaybackMode.REPEAT -> {
                    shuffleModeEnabled = false
                    repeatMode = Player.REPEAT_MODE_ALL
                }

                PlaybackMode.REPEAT_ONE -> {
                    repeatMode = Player.REPEAT_MODE_ONE
                }

                PlaybackMode.SHUFFLE -> {
                    repeatMode = Player.REPEAT_MODE_ALL
                    shuffleModeEnabled = true
                }
            }

            _musicState.update {
                it.copy(
                    playbackMode = repeatModee,
                    )
            }
        }
    }

    private fun startMediaPlayback(
        songs: List<Song>,
        startIndex: Int = 0,
        startPosition: Long = 0L
    ) {
        mediaController?.run {
            setMediaItems(songs.map { it.asMediaItem() }, startIndex, startPosition)
            prepare()
            play()
        }
    }

    fun stopController() {
        mediaController?.clearMediaItems()
        mediaController?.stop()
        mediaController?.release()
        mediaRepository.setPlayingQueueIds(emptyList())
    }

    fun itemRemoved(index: Int) {
        mainCoroutineScope.launch {
            mediaController?.removeMediaItem(index)
        }
    }


}

private fun Int.asPlaybackMode(): PlaybackMode {
    return when (this) {
        Player.REPEAT_MODE_ALL -> PlaybackMode.REPEAT
        Player.REPEAT_MODE_ONE -> PlaybackMode.REPEAT_ONE
        else -> PlaybackMode.SHUFFLE
    }
}

package com.mmusic.player.music

import androidx.media3.common.Player
import com.mmusic.player.domain.model.PlaybackMode

data class MusicState(
    val mediaId: String = "",
    val currentSongIndex: Int = 0,
    val playbackState: PlaybackState = PlaybackState.IDLE,
    val playWhenReady: Boolean = false,
    val duration: Long = 0,
    val playbackMode: PlaybackMode = PlaybackMode.REPEAT,
)

enum class PlaybackState { IDLE, BUFFERING, READY, ENDED }

fun Int.asPlaybackState() = when (this) {
    Player.STATE_IDLE -> PlaybackState.IDLE
    Player.STATE_BUFFERING -> PlaybackState.BUFFERING
    Player.STATE_READY -> PlaybackState.READY
    Player.STATE_ENDED -> PlaybackState.ENDED
    else -> error("Something went wrong")
}
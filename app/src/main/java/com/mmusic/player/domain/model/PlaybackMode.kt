package com.mmusic.player.domain.model

import com.mmusic.player.R

enum class PlaybackMode {
    REPEAT,
    REPEAT_ONE,
    SHUFFLE,;

    fun getDrawable(): Int? {
        return when (this) {
            REPEAT -> R.drawable.baseline_repeat_24
            REPEAT_ONE -> R.drawable.baseline_repeat_one_24
            SHUFFLE -> R.drawable.baseline_shuffle_24
        }
    }
}

fun PlaybackMode.togglePlaybackMode() :PlaybackMode{
    return when(this){
        PlaybackMode.REPEAT-> PlaybackMode.REPEAT_ONE
        PlaybackMode.REPEAT_ONE-> PlaybackMode.SHUFFLE
        PlaybackMode.SHUFFLE-> PlaybackMode.REPEAT
    }
}


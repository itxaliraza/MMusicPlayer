package com.mmusic.player.data

import android.content.Context
import com.mmusic.player.domain.model.PlaybackMode
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefHelper @Inject constructor(@ApplicationContext context: Context) {
    val sharedPreferences = context.getSharedPreferences("mpref", Context.MODE_PRIVATE)

    var repeatMode: PlaybackMode
        get() {
            val mode = sharedPreferences.getInt("repeatMode", 0)
            return PlaybackMode.values()[mode]
        }
        set(value) {
            sharedPreferences.edit().putInt("repeatMode", value.ordinal).apply()
        }
}
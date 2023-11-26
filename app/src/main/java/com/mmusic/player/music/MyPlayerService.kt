package com.mmusic.player.music

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.os.Build
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.mmusic.player.MainActivity
import com.mmusic.player.data.PrefHelper
import com.mmusic.player.domain.model.PlaybackMode
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@UnstableApi
@AndroidEntryPoint
class MyPlayerService :MediaSessionService() {

    var mediaSession: MediaSession? = null

    @Inject
    lateinit var player: ExoPlayer

    @Inject
    lateinit var mediaSessionCallback: MediaSessionCallback

    @Inject
    lateinit var musicNotificationProvider: MusicNotificationProvider

    @Inject
    lateinit var musicServiceConnection: MusicServiceConnection

    @Inject
    lateinit var prefHelper: PrefHelper


    override fun onCreate() {
        super.onCreate()

        setMediaNotificationProvider(musicNotificationProvider)
        val sessionActivityPendingIntent: PendingIntent = TaskStackBuilder.create(this).run {
            addNextIntent(
                Intent(
                    this@MyPlayerService,
                    MainActivity::class.java
                )
            )
            val immutableFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE
            } else 0

            getPendingIntent(0, immutableFlag or PendingIntent.FLAG_UPDATE_CURRENT)
        }

        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(sessionActivityPendingIntent)
            .setCallback(mediaSessionCallback)
            .build()

        mediaSession?.player?.run {
            when(prefHelper.repeatMode){
                PlaybackMode.REPEAT -> {
                    repeatMode= Player.REPEAT_MODE_ALL
                }
                PlaybackMode.REPEAT_ONE -> {
                    repeatMode=Player.REPEAT_MODE_ONE
                }
                PlaybackMode.SHUFFLE -> {
                    shuffleModeEnabled=true
                    repeatMode=Player.REPEAT_MODE_ALL

                }
            }
         }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    override fun onDestroy() {
        musicServiceConnection.stopController()
        mediaSession?.run {
            release()
            player.release()
        }
        super.onDestroy()
    }
}
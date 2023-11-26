package com.mmusic.player.music

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaNotification.ActionFactory
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.common.collect.ImmutableList
import com.mmusic.player.R
import com.mmusic.player.di.MyCoroutineDispatchers
import com.mmusic.player.di.MyDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


const val MUSIC_NOTIFICATION_ID = 2102
const val MUSIC_NOTIFICATION_CHANNEL_ID = "MUSIC_NOTIFICATION_CHANNEL_ID"

@UnstableApi
class MusicNotificationProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    @MyDispatcher(MyCoroutineDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    @MyDispatcher(MyCoroutineDispatchers.MAIN) private val mainDispatcher: CoroutineDispatcher,
    private val notificationManager: NotificationManager?
) : MediaNotification.Provider {
    val musicArtworks = hashMapOf<Uri, Bitmap?>()

    private val mainCoroutine = CoroutineScope(mainDispatcher + SupervisorJob())

    override fun createNotification(
        mediaSession: MediaSession,
        customLayout: ImmutableList<CommandButton>,
        actionFactory: ActionFactory,
        onNotificationChangedCallback: MediaNotification.Provider.Callback
    ): MediaNotification {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                MUSIC_NOTIFICATION_CHANNEL_ID,
                "Songs",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager?.createNotificationChannel(channel)
        }

        val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(
                context,
                MUSIC_NOTIFICATION_CHANNEL_ID
            )
        } else
            NotificationCompat.Builder(context)


        val player = mediaSession.player
        val metaData = player.mediaMetadata

        notificationBuilder.setContentTitle(metaData.title)
            .setContentText(metaData.artist)
            .setSmallIcon(R.drawable.ic_music_icon)
            .setContentIntent(mediaSession.sessionActivity)
            .setStyle(
                MediaStyleNotificationHelper.MediaStyle(mediaSession)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setContentIntent(mediaSession.sessionActivity)


        val prevAction = createMediaNotification(
            context,
            actionFactory,
            mediaSession,
            R.drawable.baseline_skip_previous_24,
            "Prev",
            Player.COMMAND_SEEK_TO_PREVIOUS
        )

        val playPauseAction = createMediaNotification(
            context,
            actionFactory,
            mediaSession,
            if (player.playWhenReady) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24,
            if (player.playWhenReady) "Play" else "Pause",
            Player.COMMAND_PLAY_PAUSE
        )

        val nextAction = createMediaNotification(
            context,
            actionFactory,
            mediaSession,
            R.drawable.baseline_skip_next_24,
            "Next",
            Player.COMMAND_SEEK_TO_NEXT
        )

        val exitAction = actionFactory.createCustomActionFromCustomCommandButton(
            mediaSession,
            customLayout.get(0)
        )

        notificationBuilder.addAction(prevAction)
        notificationBuilder.addAction(playPauseAction)
        notificationBuilder.addAction(nextAction)
        notificationBuilder.addAction(exitAction)

        if (musicArtworks[metaData.artworkUri] != null) {
            notificationBuilder.setLargeIcon(musicArtworks[metaData.artworkUri])
        } else {
            getMusicArtiBitmap(metaData.artworkUri) {
                notificationBuilder.setLargeIcon(it)
                onNotificationChangedCallback.onNotificationChanged(
                    MediaNotification(
                        MUSIC_NOTIFICATION_ID, notificationBuilder.build()
                    )
                )
            }
        }


        return MediaNotification(MUSIC_NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getMusicArtiBitmap(uri: Uri?, bitmapLoaded: (Bitmap?) -> Unit) {
        mainCoroutine.launch {
            var bitmap: Bitmap? = null
            withContext(Dispatchers.IO) {
                val imageRequest = ImageRequest.Builder(context)
                    .placeholder(R.drawable.music_new_noti)
                    .error(R.drawable.music_new_noti)
                    .data(uri)
                    .build()
                bitmap = ImageLoader(context).execute(imageRequest).drawable?.toBitmap()
            }
            bitmapLoaded(bitmap)
            if (uri != null)
                musicArtworks[uri] = bitmap
        }
    }

    override fun handleCustomCommand(
        session: MediaSession,
        action: String,
        extras: Bundle
    ): Boolean = false


    private fun createMediaNotification(
        context: Context,
        actionFactory: ActionFactory,
        mediaSession: MediaSession,
        drawableId: Int,
        title: String,
        command: Int
    ): NotificationCompat.Action {
        return actionFactory.createMediaAction(
            mediaSession,
            IconCompat.createWithResource(context, drawableId),
            title,
            command
        )
    }
}


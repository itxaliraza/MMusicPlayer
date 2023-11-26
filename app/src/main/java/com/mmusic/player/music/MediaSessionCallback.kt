package com.mmusic.player.music

import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.mmusic.player.R
import javax.inject.Inject

@UnstableApi
class MediaSessionCallback @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
) : MediaSession.Callback {
    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): MediaSession.ConnectionResult {
        val connectionResult = super.onConnect(session, controller)
        val availableSessionCommands = connectionResult.availableSessionCommands.buildUpon()
        val commandButton = exitCommandButton()

        commandButton.sessionCommand?.let {
            availableSessionCommands.add(it)
        }
        return MediaSession.ConnectionResult.accept(
            availableSessionCommands.build(),
            connectionResult.availablePlayerCommands
        )
    }

    override fun onPostConnect(session: MediaSession, controller: MediaSession.ControllerInfo) {
        val commandButton = exitCommandButton()

        session.setCustomLayout(
            controller, listOf(
                commandButton
            )
        )
    }


    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {
        musicServiceConnection.stopController()
        return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
    }


    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>
    ): ListenableFuture<List<MediaItem>> = Futures.immediateFuture(
        mediaItems.map { it.buildUpon().setUri(it.requestMetadata.mediaUri).build() }
    )


}


private fun exitCommandButton(): CommandButton {
    return CommandButton.Builder()
        .setDisplayName("Exit")
        .setSessionCommand(SessionCommand("Exit", Bundle.EMPTY))
        .setIconResId(R.drawable.baseline_close_24)
        .build()
}
package com.mmusic.player.domain.model

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

data class Song(
    val mediaId: String,
    val title: String,
    val artist: String,
    val album: String,
    val folder: String,
    val duration: Long,
    val date: Long,
    val artistId: Long,
    val albumId: Long,
    val mediaUri: Uri,
    val artworkUri: Uri,
    val durationValue: String,
    val size: Long
)

fun Song.asMediaItem(): MediaItem {
    return MediaItem.Builder()
        .setMediaId(mediaId)
        .setUri(mediaUri)
        .setRequestMetadata(
            MediaItem.RequestMetadata.Builder()
                .setMediaUri(mediaUri)
                .build()
        )
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setArtworkUri(artworkUri)
                .setTitle(title)
                .setArtist(artist)
                .setIsBrowsable(false)
                .setIsPlayable(true)
                .build()
        )
        .build()
}
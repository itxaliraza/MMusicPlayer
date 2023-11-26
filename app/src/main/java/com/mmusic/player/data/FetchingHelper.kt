package com.mmusic.player.data

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.TimeUnit


val mediaCollection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
} else {
    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
}
val projection = arrayOf(
    MediaStore.Audio.Media._ID,
    MediaStore.Audio.Media.ARTIST_ID,
    MediaStore.Audio.Media.ALBUM_ID,
    MediaStore.Audio.Media.TITLE,
    MediaStore.Audio.Media.ARTIST,
    MediaStore.Audio.Media.ALBUM,
    MediaStore.Audio.Media.DATE_MODIFIED,
    MediaStore.Audio.Media.DURATION,
    MediaStore.Audio.Media.DATA,
    MediaStore.Audio.Media.SIZE,
)

fun Long.getDurationValue(): String {
    val hours = TimeUnit.MILLISECONDS.toHours(this)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) % 60

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else String.format("%02d:%02d", minutes, seconds)

}


fun ContentResolver.observe(uri: Uri) = callbackFlow {
    val observer = object : ContentObserver(null) {
        override fun onChange(selfChange: Boolean) {
            trySend(selfChange)
        }
    }
    registerContentObserver(uri, true, observer)

    trySend(true)

    awaitClose {
        unregisterContentObserver(observer)
    }
}
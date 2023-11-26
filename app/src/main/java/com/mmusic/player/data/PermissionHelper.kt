package com.mmusic.player.data

import android.os.Build

val getReadAudioPermission =if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU)
    android.Manifest.permission.READ_MEDIA_AUDIO
else
    android.Manifest.permission.READ_EXTERNAL_STORAGE



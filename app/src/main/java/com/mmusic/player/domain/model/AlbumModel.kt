package com.mmusic.player.domain.model

import android.net.Uri

data class AlbumModel(
    val albumId:Long,
    val album:String,
    val artist:String,
    val artWorkUri:Uri,
    val songsList: ArrayList<Song>
)
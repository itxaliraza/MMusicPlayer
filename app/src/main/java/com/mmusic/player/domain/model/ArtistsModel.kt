package com.mmusic.player.domain.model

import android.net.Uri

data class ArtistsModel(
    val artistId:Long,
    val artistName:String,
    val artworkUri:Uri,
    val songsList:ArrayList<Song>
)
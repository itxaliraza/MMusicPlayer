package com.mmusic.player.ui.navigation

import com.mmusic.player.R

enum class BottomDestination(
    val route: String,
    val title: String,
    val icon: Any
) {
    Music(
        Screen.MusicScreen.route,
        "Songs",
        R.drawable.baseline_music_note_24
    ),
    Album(
        Screen.AlbumScreen.route,
        "Albums",
        R.drawable.ic_album
    ),
    Artist(
        Screen.ArtistScreen.route,
        "Artists",
        R.drawable.artist
    ),

    Folders(
        Screen.FolderScreen.route,
        "Folders",
        R.drawable.folder
    )

}
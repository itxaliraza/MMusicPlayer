package com.mmusic.player.ui.navigation

sealed class Screen(val route: String) {
    data object MusicScreen : Screen("MusicScreen")
    data object ArtistScreen : Screen("ArtistScreen")
    data object AlbumScreen : Screen("AlbumScreen")
    data object FolderScreen : Screen("FolderScreen")
    data object PlayerScreen : Screen("PlayerScreen")
    data object AlbumDetailScreen : Screen("$AlbumDetailScreenRoute/{$ALBUM_ID_ARGS}")
    data object ArtistDetailScreen : Screen("$ARTIST_DETAILS_SCREEN_ROUTE/{$ARTIST_ID_ARGS}")
    data object FolderDetailScreen : Screen("$FOLDER_DETAILS_SCREEN_ROUTE/{$FOLDER_ID_ARGS}")

    companion object{
        const val ALBUM_ID_ARGS = "ALBUM_ID"
        const val AlbumDetailScreenRoute = "AlbumDetailScreen"

        const val ARTIST_ID_ARGS = "ARTIST_ID"
        const val ARTIST_DETAILS_SCREEN_ROUTE = "ARTIST_DETAILS_SCREEN_ROUTE"

        const val FOLDER_ID_ARGS = "FOLDER_ID"
        const val FOLDER_DETAILS_SCREEN_ROUTE = "FOLDER_DETAILS_SCREEN_ROUTE"
    }
}


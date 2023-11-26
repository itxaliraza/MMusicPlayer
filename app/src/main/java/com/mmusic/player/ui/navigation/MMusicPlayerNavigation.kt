package com.mmusic.player.ui.navigation

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mmusic.player.ui.albumdetails.AlbumDetailsScreen
import com.mmusic.player.ui.albums.AlbumsScreen
import com.mmusic.player.ui.artists.ArtistsScreen
import com.mmusic.player.ui.artistsdetails.ArtistDetailsScreen
import com.mmusic.player.ui.folderdetails.FolderDetailsScreen
import com.mmusic.player.ui.folders.FoldersScreen
import com.mmusic.player.ui.navigation.Screen.Companion.ALBUM_ID_ARGS
import com.mmusic.player.ui.navigation.Screen.Companion.ARTIST_DETAILS_SCREEN_ROUTE
import com.mmusic.player.ui.navigation.Screen.Companion.ARTIST_ID_ARGS
import com.mmusic.player.ui.navigation.Screen.Companion.AlbumDetailScreenRoute
import com.mmusic.player.ui.navigation.Screen.Companion.FOLDER_DETAILS_SCREEN_ROUTE
import com.mmusic.player.ui.navigation.Screen.Companion.FOLDER_ID_ARGS
import com.mmusic.player.ui.player.PlayerActivity
import com.mmusic.player.ui.player.PlayerScreen
import com.mmusic.player.ui.songs.SongsScreen

@UnstableApi
@Composable
fun MMusicPlayerNavNavHost(
    activity:ComponentActivity,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String
) {

    NavHost(
        modifier = modifier, startDestination = startDestination, navController = navController
    ) {
        composable(route = Screen.MusicScreen.route) {
            SongsScreen{
                activity.startActivity(Intent(activity,PlayerActivity::class.java))
               // navController.navigate(Screen.PlayerScreen.route)
            }
        }
        composable(route = Screen.PlayerScreen.route) {
            PlayerScreen {
                navController.popBackStack()
            }
        }


        composable(route = Screen.ArtistScreen.route) {
            ArtistsScreen {
                navController.navigate("$ARTIST_DETAILS_SCREEN_ROUTE/$it")
            }
        }
        composable(route = Screen.AlbumScreen.route) {
            AlbumsScreen {
                navController.navigate("$AlbumDetailScreenRoute/$it")
            }
        }
        composable(route = Screen.FolderScreen.route) {
            FoldersScreen {

                val folderPath = "$FOLDER_DETAILS_SCREEN_ROUTE/${Uri.encode(it)}"
                Log.d("cvvr", folderPath)
                navController.navigate(folderPath)
            }
        }
        composable(
            route = Screen.AlbumDetailScreen.route,
            arguments = listOf(navArgument(name = ALBUM_ID_ARGS) {
                type = NavType.LongType
            })
        ) {
            val albumId = it.arguments!!.getLong(ALBUM_ID_ARGS)
            AlbumDetailsScreen(id = albumId) {
                navController.popBackStack()
            }
        }
        composable(
            route = Screen.ArtistDetailScreen.route,
            arguments = listOf(navArgument(name = ARTIST_ID_ARGS) {
                type = NavType.LongType
            })
        ) {
            val artistId:Long = it.arguments!!.getLong(ARTIST_ID_ARGS)
            Log.d("Cvvrr","artistId =$artistId")

            ArtistDetailsScreen(id = artistId) {
                navController.popBackStack()
            }
        }

        composable(
            route = Screen.FolderDetailScreen.route,
            arguments = listOf(navArgument(name = FOLDER_ID_ARGS) {
                type = NavType.StringType
            })
        ) {
            val folderPath = Uri.decode(it.arguments!!.getString(FOLDER_ID_ARGS) ?: "")
            Log.d("Cvvrr","folder patg =$folderPath")
            FolderDetailsScreen(path = folderPath) {
                navController.popBackStack()
            }
        }

    }

}

fun NavBackStackEntry.checkIfDestinationIsBottom(): Boolean {
    val destinationRoute = this.destination.route
    val bottomDestinations = setOf(
        Screen.MusicScreen.route,
        Screen.AlbumScreen.route,
        Screen.ArtistScreen.route,
        Screen.FolderScreen.route
    )
    return destinationRoute in bottomDestinations

}

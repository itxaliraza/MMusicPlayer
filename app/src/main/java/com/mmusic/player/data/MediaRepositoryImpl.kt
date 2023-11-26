package com.mmusic.player.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.mmusic.player.di.MyCoroutineDispatchers
import com.mmusic.player.di.MyDispatcher
import com.mmusic.player.domain.MediaRepository
import com.mmusic.player.domain.model.AlbumModel
import com.mmusic.player.domain.model.ArtistsModel
import com.mmusic.player.domain.model.FolderModel
import com.mmusic.player.domain.model.Song
import com.mmusic.player.domain.model.SortModel
import com.mmusic.player.domain.model.SortOrder
import com.mmusic.player.domain.model.SortType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @MyDispatcher(MyCoroutineDispatchers.MAIN) private val ioDispatcher: CoroutineDispatcher
) : MediaRepository {

    val coroutineScope = CoroutineScope(ioDispatcher + SupervisorJob())

    private var _playingQueueIds: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())

    private var _albums: MutableStateFlow<HashMap<Long, AlbumModel>> =
        MutableStateFlow(hashMapOf())
    val albums = _albums.asStateFlow()

    private var _artists: MutableStateFlow<HashMap<Long, ArtistsModel>> =
        MutableStateFlow(HashMap())
    val artists = _artists.asStateFlow()

    private var _folders: MutableStateFlow<HashMap<String, FolderModel>> =
        MutableStateFlow(HashMap())
    val folders: StateFlow<Map<String, FolderModel>> = _folders.asStateFlow()

    private var _songsMap: MutableStateFlow<HashMap<String, Song>> =
        MutableStateFlow(HashMap())
    val songsMap: StateFlow<Map<String, Song>> = _songsMap.asStateFlow()


    private val contentResolver = context.contentResolver

      var isFetching = false

    override suspend fun getSongs(): Flow<ArrayList<Song>> =
        contentResolver.observe(mediaCollection).map {
            isFetching = true
            Log.d("cvvr", "started fetching songs")
            withContext(Dispatchers.IO) {

                val albumsHashMap = HashMap<Long, AlbumModel>()
                val artistsHashMap = HashMap<Long, ArtistsModel>()
                val foldersHashMap = HashMap<String, FolderModel>()
                val songsHashMap = HashMap<String, Song>()

                val songsList = arrayListOf<Song>()
                contentResolver.query(
                    mediaCollection,
                    projection,
                    "${MediaStore.Audio.Media.IS_MUSIC} != 0",
                    null,
                    null
                )?.use { cursor ->
                    val idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                    val artistIdColumnIndex =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
                    val albumIdColumnIndex =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                    val titleColumnIndex =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                    val artistColumnIndex =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                    val albumColumnIndex =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                    val dateModifiedColumnIndex =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)
                    val durationColumnIndex =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                    val dataColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

                    val sizeColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)


                    while (cursor.moveToNext()) {
                        try {
                            val id = cursor.getLong(idColumnIndex)
                            val artistId = cursor.getLong(artistIdColumnIndex)
                            val albumId = cursor.getLong(albumIdColumnIndex)

                            val title = cursor.getString(titleColumnIndex)

                            val artist = cursor.getString(artistColumnIndex)

                            val album = cursor.getString(albumColumnIndex)

                            val dateModified = cursor.getLong(dateModifiedColumnIndex)

                            val duration = cursor.getLong(durationColumnIndex)


                            val data = cursor.getString(dataColumnIndex)

                            val mediaUri = ContentUris.withAppendedId(
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id
                            )

                            val artWorkUri = ContentUris.withAppendedId(
                                Uri.parse("content://media/external/audio/albumart"), albumId
                            )

                            val size = cursor.getLong(sizeColumnIndex)

                            val folder = File(data).parentFile
                            val folderName = folder?.name ?: "Unknown"
                            val folderPath = folder?.path ?: "Unknown"
                            val song = Song(
                                id.toString(),
                                title,
                                artist,
                                album,
                                folderName,
                                duration,
                                dateModified,
                                artistId,
                                albumId,
                                mediaUri,
                                artWorkUri,
                                duration.getDurationValue(),
                                size
                            )


                            addAlbumItem(
                                albumsHashMap,
                                albumId,
                                album,
                                artist,
                                artWorkUri,
                                song
                            )
                            addArtistItem(artistsHashMap, artistId, artist, artWorkUri, song)
                            addFolderItem(foldersHashMap, folderPath, folderName, song)
                            songsHashMap[id.toString()] = song
                            songsList.add(song)
                        } catch (e: Exception) {
                            Log.d("cvvr", "Exception =${e.message}")
                        }
                    }
                }

                _albums.value = albumsHashMap
                _artists.value = artistsHashMap
                _folders.value = foldersHashMap
                _songsMap.value = songsHashMap
                Log.d("cvvr", "complerted fetching songs ${songsList.size}")
                isFetching = false
                return@withContext songsList
            }
        }

    private fun addArtistItem(
        artistsHashMap: HashMap<Long, ArtistsModel>,
        artistId: Long,
        artist: String,
        artWorkUri: Uri,
        song: Song
    ) {
        var lastArtist: ArtistsModel? = artistsHashMap[artistId]
        if (lastArtist == null) {
            lastArtist = ArtistsModel(artistId, artist, artWorkUri, arrayListOf())
        }
        lastArtist.songsList.add(song)
        artistsHashMap[artistId] = lastArtist
    }

    private fun addFolderItem(
        foldersHashMap: HashMap<String, FolderModel>,
        path: String,
        folderName: String,
        song: Song
    ) {

        var lastFolder: FolderModel? = foldersHashMap[path]
        if (lastFolder == null) {
            lastFolder = FolderModel(path, folderName, arrayListOf())
        }
        lastFolder.songsList.add(song)

        foldersHashMap[path] = lastFolder
    }


    private fun addAlbumItem(
        albumsHashMap: HashMap<Long, AlbumModel>,
        albumId: Long,
        album: String,
        artist: String,
        artWorkUri: Uri,
        song: Song
    ) {
        var lastAlbum: AlbumModel? = albumsHashMap[albumId]
        if (lastAlbum == null) {
            lastAlbum = AlbumModel(albumId, album, artist, artWorkUri, arrayListOf())
        }
        lastAlbum.songsList.add(song)
        albumsHashMap[albumId] = lastAlbum
    }


    suspend fun sortSongs(
        currentList: List<Song>,
        sortModel: SortModel,
        sortingDone: (List<Song>) -> Unit
    ) {
        val currentMutableList = currentList.toMutableList()
        withContext(ioDispatcher) {

            when (sortModel.sortOrder) {
                SortOrder.Ascending -> {
                    when (sortModel.sortType) {
                        SortType.TITLE -> currentMutableList.sortBy { it.title }
                        SortType.ARTIST -> currentMutableList.sortBy { it.artist }
                        SortType.ALBUM -> currentMutableList.sortBy { it.album }
                        SortType.DATE_MODIFIED -> currentMutableList.sortBy { it.date }
                        SortType.DURATION -> currentMutableList.sortBy { it.duration }
                        SortType.SIZE -> currentMutableList.sortBy { it.size }
                    }
                }

                SortOrder.Descending -> {
                    when (sortModel.sortType) {
                        SortType.TITLE -> currentMutableList.sortByDescending { it.title }
                        SortType.ARTIST -> currentMutableList.sortByDescending { it.artist }
                        SortType.ALBUM -> currentMutableList.sortByDescending { it.album }
                        SortType.DATE_MODIFIED -> currentMutableList.sortByDescending { it.date }
                        SortType.DURATION -> currentMutableList.sortByDescending { it.duration }
                        SortType.SIZE -> currentMutableList.sortByDescending { it.size }
                    }
                }
            }

            Log.d("cvvr", "songs list= $currentMutableList")


            sortingDone(currentMutableList.toList())

        }
    }

    fun setPlayingQueueIds(list: List<String>) {
        _playingQueueIds.value = list
    }


    fun getPlayingQueueSongsFlow(): Flow<List<Song>> {
        return _playingQueueIds.map { ids ->
            ids.mapNotNull {
                songsMap.value[it]
            }
        }
    }

    suspend fun getPlayingQueueSongs(): List<Song> {
        return getPlayingQueueSongsFlow().first()
    }

    fun removeItemFromPlayingQueue(index: Int) {
        val list = _playingQueueIds.value.toMutableList()
        list.removeAt(index)
        _playingQueueIds.value = list
    }

    fun getCurrentSongPlayed(mediaId: String): Song? {
        return songsMap.value[mediaId]
    }


}


package com.mmusic.player.domain

import com.mmusic.player.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
     suspend fun getSongs(): Flow<List<Song>>
}
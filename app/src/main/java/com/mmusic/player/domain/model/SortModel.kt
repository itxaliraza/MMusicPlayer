package com.mmusic.player.domain.model

data class SortModel(
    val sortOrder: SortOrder = SortOrder.Ascending,
    val sortType: SortType = SortType.TITLE
)

val sortTypeMap = mapOf(
    SortType.TITLE to "Title",
    SortType.ARTIST to "Artist",
    SortType.ALBUM to "Album",
    SortType.DATE_MODIFIED to "Date Modified",
    SortType.DURATION to "Duration",
    SortType.SIZE to "Size"
)

val albumArtistSortTypeMap = mapOf(
    SortType.TITLE to "Title",
    SortType.DATE_MODIFIED to "Date Modified",
    SortType.DURATION to "Duration",
    SortType.SIZE to "Size"
)





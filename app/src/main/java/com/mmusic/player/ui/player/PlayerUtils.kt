package com.mmusic.player.ui.player


fun convertToProgress(count: Long, total: Long): Float =
    ((count * 100f) / total / 100f).takeIf(Float::isFinite) ?: 0f

fun Long.toTimeString(): String {
    val isNegative = this < 0
    val absoluteValue = if (isNegative) 0 else this

    val hours = absoluteValue / (1000 * 60 * 60)
    val minutes = (absoluteValue % (1000 * 60 * 60)) / (1000 * 60)
    val seconds = (absoluteValue % (1000 * 60)) / 1000

    val formattedHours = if (hours > 0) "$hours:" else ""
    val formattedMinutes = String.format("%02d", minutes)
    val formattedSeconds = String.format("%02d", seconds)

    return "$formattedHours$formattedMinutes:$formattedSeconds"
}

internal fun convertToPosition(value: Float, total: Long) = (value * total).toLong()







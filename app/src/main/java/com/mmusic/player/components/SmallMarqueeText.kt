package com.mmusic.player.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import ir.kaaveh.sdpcompose.ssp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SmallMarqueeText(modifier: Modifier, text: String) {
    Text(
         text = text,
        fontSize = 11.ssp,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        modifier = modifier.basicMarquee()
    )
}
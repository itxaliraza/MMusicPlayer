package com.mmusic.player.components

import androidx.compose.foundation.layout.Box
 import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun CircularLoading(modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center){
        CircularProgressIndicator()

    }
}
package com.mmusic.player.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.mmusic.player.R

@Composable
fun SortingIconButton(modifier: Modifier = Modifier, sortClicked: () -> Unit) {
    IconButton(modifier = modifier, onClick = { sortClicked() }) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_sort_24),
            contentDescription = null
        )
    }
}
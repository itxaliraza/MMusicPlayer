package com.mmusic.player.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mmusic.player.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun ShuffleAll(modifier: Modifier=Modifier,total: Int,shuffleClicked:()->Unit) {
    Row(modifier = modifier.clickable {
        shuffleClicked()
    }.padding(horizontal = 5.sdp, vertical = 5.sdp)) {
         CircularPlayBtn()
        Spacer(modifier = Modifier.width(10.sdp))
        Text(text = stringResource(R.string.shuffle_all, total), fontSize = 14.ssp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CircularPlayBtn() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(MaterialTheme.colorScheme.primary)
     ) {
        Icon(modifier = Modifier.size(25.sdp).padding(5.sdp), imageVector = Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White)
    }
}
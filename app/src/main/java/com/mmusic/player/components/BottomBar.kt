package com.mmusic.player.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.mmusic.player.ui.navigation.BottomDestination
import ir.kaaveh.sdpcompose.sdp

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MBottomBar(
    modifier: Modifier = Modifier,
    selectedItem: String,
    routeSelected: (BottomDestination) -> Unit
) {


    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            BottomDestination.values().forEachIndexed { index, item ->
                Column(
                    modifier = Modifier
                        .clickable {
                            routeSelected(item)
                        }
                        .width(70.sdp)
                        .height(40.sdp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val colorFilter = if (selectedItem != item.title)
                        ColorFilter.tint(color = Color.Gray)
                    else ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
                    GlideImage(
                        modifier = Modifier
                            .size(27.sdp),
                        model = item.icon,
                        contentDescription = item.name,
                        colorFilter = colorFilter
                    )
                    Spacer(modifier = Modifier.height(4.sdp))
                    if (selectedItem == item.title)
                        HorizontalDivider(
                            modifier = Modifier
                                .height(1.sdp)
                                .width(30.sdp),
                            thickness = 5.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                }
            }
        }
    }
}
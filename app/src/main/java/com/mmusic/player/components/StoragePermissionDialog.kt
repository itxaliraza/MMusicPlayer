package com.mmusic.player.components

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import ir.kaaveh.sdpcompose.sdp


@Composable
fun PermissionDialog(text:String,onDismiss:()->Unit) {
    val context= LocalContext.current
    Dialog(onDismissRequest = {
       onDismiss()
    }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.sdp))
                .background(Color.White)
                .padding(10.sdp)
        ) {

            Text(text = text)


            Spacer(modifier = Modifier.height(16.sdp))
            Button(onClick = {
               onDismiss()
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                ).also {
                    context.startActivity(it)
                }
            }) {
                Text(text = "Go To Settings")
            }

        }
    }
}
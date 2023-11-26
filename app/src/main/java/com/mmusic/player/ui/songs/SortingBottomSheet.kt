package com.mmusic.player.ui.songs

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mmusic.player.domain.model.SortModel
import com.mmusic.player.domain.model.SortOrder
import com.mmusic.player.domain.model.albumArtistSortTypeMap
 import com.mmusic.player.domain.model.sortTypeMap
import ir.kaaveh.sdpcompose.sdp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortingBottomSheet(
    sortModel: SortModel = SortModel(),
    onDismiss: () -> Unit,
    sortingApplied: (SortModel) -> Unit,
    isForArtistOrAlbums:Boolean=false
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedSortingModel by remember {
        mutableStateOf(
            sortModel
        )
    }

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.sdp)) {
            Text(
                text = "Sort By",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.sdp))
            HorizontalDivider(thickness = 1.sdp)
          val list=  if (isForArtistOrAlbums)
                albumArtistSortTypeMap
            else
                sortTypeMap

            list.forEach { (sortType, sortTypeValue) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (selectedSortingModel.sortType == sortType), onClick = {
                                Log.d(
                                    "cvvr",
                                    "Selected sortmodel =$selectedSortingModel, ${sortType}"
                                )
                                selectedSortingModel =
                                    selectedSortingModel.copy(sortType = sortType)
                            }
                        )
                        .padding(start = 10.dp)
                ) {
                    Text(
                        text = sortTypeValue,
                        color = if (selectedSortingModel.sortType == sortType)
                            MaterialTheme.colorScheme.primary.copy(blue = 0.7f)
                        else Color.Black,
                        fontWeight = if (selectedSortingModel.sortType == sortType) FontWeight.Bold else null
                    )

                    RadioButton(
                        selected = (selectedSortingModel.sortType == sortType),
                        onClick = {
                            selectedSortingModel =
                                selectedSortingModel.copy(sortType = sortType)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.sdp))


            HorizontalDivider(thickness = 1.sdp)

            Spacer(modifier = Modifier.height(6.sdp))

            SortOrder.values().forEach { sortOrder ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (sortOrder == selectedSortingModel.sortOrder), onClick = {
                                selectedSortingModel =
                                    selectedSortingModel.copy(sortOrder = sortOrder)
                            }
                        )
                        .padding(start = 10.dp),
                ) {
                    Text(
                        text = sortOrder.name,
                        color = if (sortOrder == selectedSortingModel.sortOrder)
                            MaterialTheme.colorScheme.primary.copy(blue = 0.7f)
                        else Color.Black,
                        fontWeight = if (sortOrder == selectedSortingModel.sortOrder) FontWeight.Bold else null
                    )

                    RadioButton(
                        selected = (sortOrder == selectedSortingModel.sortOrder),
                        onClick = {
                            selectedSortingModel =
                                selectedSortingModel.copy(sortOrder = sortOrder)
                        })
                }
            }

            Spacer(modifier = Modifier.height(16.sdp))


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.sdp, vertical = 21.sdp)
            ) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.sdp),
                    onClick = {
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)

                ) {
                    Text(text = "Cancel")
                }
                Spacer(modifier = Modifier.width(10.sdp))

                Button(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.sdp),
                    onClick = {
                        sortingApplied(selectedSortingModel)
                        onDismiss()
                    }) {
                    Text(text = "Apply")
                }
            }

        }
    }


}
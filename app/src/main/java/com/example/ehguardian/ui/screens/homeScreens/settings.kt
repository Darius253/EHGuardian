package com.example.ehguardian.ui.screens.homeScreens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPopUp(
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(

        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.medium,
        sheetState = sheetState,
        dragHandle = {

            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // IconButton to close the popup
                    IconButton(

                        onClick = onDismiss
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Close Icon")
                    }

                    // Spacer to push the text towards the center
                    Box(
                        modifier = Modifier
                            .weight(1f) // This ensures that the text is centered
                            .padding(end = 48.dp), // To account for the size of the IconButton
                        contentAlignment = Alignment.Center // Centering the text in the Box
                    ) {
                        Text(
                            text = "SETTINGS",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }

                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }


            }


        ) {
        Column (
            modifier= Modifier
                .padding(16.dp)
                .fillMaxSize(),
        ){

        }


    }
}

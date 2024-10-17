@file:OptIn(ExperimentalMaterial3Api::class)

package com.trontech.ehguardian.ui.screens.homeScreens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable

fun PostNotificationPopUp(
    sheetState : SheetState,
    onDismiss: () -> Unit
){
    var checked by remember { mutableStateOf(false) }
    val context = LocalContext.current


    ModalBottomSheet(
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetState = sheetState,
        dragHandle = {
            ModalBottomHeader("Post-Notifications", onDismiss)
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxHeight(0.45f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .fillMaxWidth()
            ) {
                Text(text = "Enable Post Notifications",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(start = 16.dp))

                Switch(
                    modifier = Modifier.padding(end = 16.dp),
                    checked = checked, onCheckedChange = {
                        checked = it
                      if (checked)  Toast.makeText(context, "Post notifications enabled", Toast.LENGTH_SHORT).show()
                        else Toast.makeText(context, "Post notifications disabled", Toast.LENGTH_SHORT).show()
                    })
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "We will send you post notifications to remind you to record your health data and other important information. ",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                )
        }

    }

}

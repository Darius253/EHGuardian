@file:OptIn(ExperimentalMaterial3Api::class)

package com.trontech.ehguardian.ui.screens.homeScreens.settings.postNotifications

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.trontech.ehguardian.ui.screens.homeScreens.settings.ModalBottomHeader

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun PostNotificationPopUp(
    sheetState: SheetState,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current

    // Observe the permission state
    var checked by rememberSaveable {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Dynamically check and update the permission state
    LaunchedEffect(Unit) {
        checked = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    ModalBottomSheet(
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetState = sheetState,
        dragHandle = {
            ModalBottomHeader("Post Notifications", onDismiss)
        }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight(0.45f),
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
                Text(
                    text = "Enable Post Notifications",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(start = 16.dp)
                )

                Switch(
                    modifier = Modifier.padding(end = 16.dp),
                    checked = checked,
                    onCheckedChange = {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        ).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                            addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                        }
                        context.startActivity(intent)

                        onDismiss()
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "We will send you post notifications to remind you to record your health data and other important information.",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

package com.trontech.ehguardian.ui.screens.homeScreens.settings

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.trontech.ehguardian.ui.AppViewModelProvider
import com.trontech.ehguardian.ui.screens.authenticationScreens.signUp.SignUpViewModel
import com.trontech.ehguardian.ui.screens.homeScreens.settings.postNotifications.PostNotificationPopUp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPopUp(
    onDismiss: () -> Unit,
    signUpViewModel: SignUpViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onSignOutSuccess: () -> Unit,
    onDeleteAccountSuccess: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var signOutPopUp by rememberSaveable { mutableStateOf(false) }
    var deleteAccountPopUp by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    var showNearbyHospitals by rememberSaveable { mutableStateOf(false) }
    val helpSheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    val postNotificationSheetState = rememberModalBottomSheetState()
    val changeLanguageSheetState = rememberModalBottomSheetState()

    if (helpSheetState.isVisible) {
        HelpAndSupportPopUp(onDismiss = { coroutineScope.launch { helpSheetState.hide() } }, helpSheetState)
    }

    if (showNearbyHospitals) {
        NearbyHospitals(onDismiss = { showNearbyHospitals = false })
    }
    if (postNotificationSheetState.isVisible) {
        PostNotificationPopUp(onDismiss = { coroutineScope.launch { postNotificationSheetState.hide() } },
            sheetState = postNotificationSheetState
        )
    }
    if (changeLanguageSheetState.isVisible) {
        ChangeLanguagePopUp(onDismissRequest = { coroutineScope.launch { changeLanguageSheetState.hide() } },
            sheetState = changeLanguageSheetState,

            )}

    ModalBottomSheet(
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetState = sheetState,
        dragHandle = {
            ModalBottomHeader("Settings", onDismiss)
        }
    ) {
        SettingsContent(
            context = context,
            onNearbyHospitalsClick = {
                if (isLocationPermissionGranted(context)) {
                    showNearbyHospitals = true
                } else {
                    Toast.makeText(context, "Location permission is required to view nearby hospitals", Toast.LENGTH_SHORT).show()
                }
            },
            onHelpClick = { coroutineScope.launch { helpSheetState.show() } },
            onLogoutClick = { signOutPopUp = true },
            signOutPopUp  = signOutPopUp,
            onDismiss = { onDismiss()
                if(deleteAccountPopUp)  deleteAccountPopUp= false

            else if (signOutPopUp) signOutPopUp= false  },
            signUpViewModel = signUpViewModel,
            onSignOutSuccess = onSignOutSuccess,
            onPostNotificationClick = { coroutineScope.launch { postNotificationSheetState.show() } },
            onChangeLanguageClick = { coroutineScope.launch { changeLanguageSheetState.show() } },
            onDeletedAccountClick = { deleteAccountPopUp = true },
            deleteAccountPopUp = deleteAccountPopUp,
            onDeletedAccountSuccess = onDeleteAccountSuccess
        )
    }
}

@Composable
fun SettingsContent(
    context: Context,
    onNearbyHospitalsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onLogoutClick: () -> Unit,
    signOutPopUp: Boolean,
    deleteAccountPopUp: Boolean,
    onDismiss: () -> Unit,
    onPostNotificationClick: () -> Unit,
    signUpViewModel: SignUpViewModel,
    onSignOutSuccess: () -> Unit,
    onChangeLanguageClick: () -> Unit,
    onDeletedAccountClick: () -> Unit,
    onDeletedAccountSuccess: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .fillMaxHeight(0.95f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            SettingsCard(
                items = listOf(
                    SettingsOption(Icons.Filled.LocalHospital, "View Hospitals Nearby", onNearbyHospitalsClick, color= MaterialTheme.colorScheme.primary),
                    SettingsOption(Icons.Filled.Notifications, "Enable Post Notifications", onPostNotificationClick,color = MaterialTheme.colorScheme.primary),
                    SettingsOption(Icons.Filled.Download, "Export All Health Data", color = MaterialTheme.colorScheme.primary),
                    SettingsOption(Icons.Filled.QuestionAnswer, "Help & Support", onHelpClick , MaterialTheme.colorScheme.primary),
                    SettingsOption(Icons.Filled.Language, "Change Language", color = MaterialTheme.colorScheme.primary, onClick = onChangeLanguageClick)
                )
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            SettingsCard(
                items = listOf(
                    SettingsOption(Icons.Filled.PrivacyTip, "Terms & Conditions", color = MaterialTheme.colorScheme.primary, onClick = {
//                        context.startActivity(
//                            android.content.Intent(
//                                android.content.Intent.ACTION_VIEW,
//                                android.net.Uri.parse("https://www.google.com")
//                            )
//                        )
                    }),
                    SettingsOption(Icons.Filled.Visibility, "Privacy Policy", color = MaterialTheme.colorScheme.primary, onClick = {
                        context.startActivity(
                            android.content.Intent(
                                android.content.Intent.ACTION_VIEW,
                                android.net.Uri.parse("https://www.termsfeed.com/live/f0ceea68-5d15-43d8-aa10-af02b4718de3")
                            )
                        )
                    }),
                    SettingsOption(Icons.Filled.Share, "Share App", color = MaterialTheme.colorScheme.primary)
                )
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            SettingsCard(
                items = listOf(
                    SettingsOption(Icons.AutoMirrored.Filled.Logout, "Logout", onClick={onLogoutClick()}, Color.Red),
                    SettingsOption(Icons.Filled.Delete, "Delete Account", color = Color.Red, onClick = {
                        onDeletedAccountClick()
                    })
                )
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Text(
                text = "Version 1.0.2",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (signOutPopUp) {
            item {
                AlertPopUp(
                    title = "Sign Out",
                    message = "Are you sure you want to sign out?",
                    confirmText = "Yes",
                    onDismiss = { onDismiss() },
                    onSuccess = {

                        signUpViewModel.signOut(onSignOutSuccess, context)
                    }
                )
            }
        }
        if (deleteAccountPopUp) {
            item {
                AlertPopUp(
                    title = "Delete Account",
                    message = "Are you sure you want to delete your account?",
                    confirmText = "Yes",
                    onDismiss = { onDismiss() },
                    onSuccess = {
                        signUpViewModel.deleteAccount(context, onDeletedAccountSuccess)
                    }
                )
            }
        }
    }
}

@Composable
fun SettingsCard(items: List<SettingsOption>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        items.forEachIndexed { index, option ->
            SettingsItem(
                leadingIcon = option.icon,
                title = option.title,
                onClick = option.onClick,
                color = option.color
            )
            if (index < items.size - 1) SettingsDivider()
        }
    }
}

@Composable
fun ModalBottomHeader(headerText: String, onDismiss: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Filled.Close, contentDescription = "Close Icon")
            }
            Box(modifier = Modifier
                .weight(1f)
                .padding(end = 48.dp), contentAlignment = Alignment.Center) {
                Text(text = headerText, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }
        HorizontalDivider(thickness = 1.dp)
    }
}

@Composable
fun SettingsItem(
    leadingIcon: ImageVector,
    title: String,
    onClick: (() -> Unit)? = null,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = Modifier
            .clickable { onClick?.invoke() }
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = leadingIcon, contentDescription = title, tint = color)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium)
        }
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = "Forward Icon", modifier = Modifier.size(16.dp))
    }
}

@Composable
fun SettingsDivider() {
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
fun AlertPopUp(
    title: String,
    message: String,
    confirmText: String,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    AlertDialog(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
        },
        text = { Text(text = message) },
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Text(
                text = confirmText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.clickable { onSuccess(); onDismiss() }
            )
        },
        dismissButton = {
            Text(
                text = "Cancel",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { onDismiss() }
                    .padding(end = 20.dp)
            )
        }
    )
}

private fun isLocationPermissionGranted(context: Context): Boolean {
    val coarsePermission = ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
    val finePermission = ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)

    return if (coarsePermission != PackageManager.PERMISSION_GRANTED && finePermission != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
            1
        )
        false
    } else true
}

data class SettingsOption(
    val icon: ImageVector,
    val title: String,
    val onClick: (() -> Unit)? = null,
    val color: Color
)

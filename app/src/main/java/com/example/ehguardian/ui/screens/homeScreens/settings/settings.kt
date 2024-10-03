package com.example.ehguardian.ui.screens.homeScreens.settings

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ehguardian.ui.AppViewModelProvider
import com.example.ehguardian.ui.screens.authenticationScreens.signUp.SignUpViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPopUp(
    onDismiss: () -> Unit,
    signUpViewModel: SignUpViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onSignOutSuccess: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var showPopUp by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    var showNearbyHospitals by rememberSaveable {
        mutableStateOf(false)
    }
    val helpSheetState = rememberModalBottomSheetState()

    val coroutineScope = rememberCoroutineScope()


    ModalBottomSheet(
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(
            topStart = 12.dp,
            topEnd = 12.dp
        ),
        sheetState = sheetState,
        dragHandle = {
            ModalBottomHeader(
                headerText = "Settings",
                onDismiss = onDismiss
            )
        }
    ) {
        if (showNearbyHospitals) {
            NearbyHospitals(onDismiss = { showNearbyHospitals = false })
        }
        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.95f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // First Card Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),

                    ) {
                    SettingsItem(
                        leadingIcon = Icons.Filled.LocalHospital,
                        title = "View Hospitals Nearby",
                        onClick = {
                            if (isLocationPermissionGranted(context)) {
                                showNearbyHospitals = true
                            } else {
                                Toast.makeText(
                                    context,
                                    "Location permission is required to view nearby hospitals",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    )
                    SettingsDivider()
                    SettingsItem(
                        leadingIcon = Icons.Filled.Notifications,
                        title = "Push Notifications",
                        onClick = { /*TODO*/ }
                    )
                    SettingsDivider()
                    SettingsItem(
                        leadingIcon = Icons.Filled.Download,
                        title = "Export All Health Data",
                        onClick = { /*TODO*/ }
                    )
                    SettingsDivider()
                    SettingsItem(
                        leadingIcon = Icons.Filled.QuestionAnswer,
                        title = "Help & Support",
                        onClick = {
                            coroutineScope.launch {
                                helpSheetState.show()
                            }



                        }
                    )
                    SettingsDivider()
                    SettingsItem(
                        leadingIcon = Icons.Filled.Language,
                        title = "Change Language",
                        onClick = { /*TODO*/ },
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Second Card Section

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),

                    ) {
                    SettingsItem(
                        leadingIcon = Icons.Filled.PrivacyTip,
                        title = "Terms & Conditions",
                        onClick = { /*TODO*/ },

                        )
                    SettingsDivider()
                    SettingsItem(
                        leadingIcon = Icons.Filled.Visibility,
                        title = "Privacy Policy",
                        onClick = { /*TODO*/ },

                        )
                    SettingsDivider()
                    SettingsItem(
                        leadingIcon = Icons.Filled.Share,
                        title = "Share App",
                        onClick = { /*TODO*/ },
                    )

                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),

                    ) {
                    SettingsItem(
                        leadingIcon = Icons.AutoMirrored.Filled.Logout,
                        title = "Logout",
                        onClick = {

                            showPopUp = true

                        },
                        color = Color.Red
                    )
                    SettingsDivider()
                    SettingsItem(
                        leadingIcon = Icons.Filled.Delete,
                        title = "Delete Account",
                        onClick = { /*TODO*/ },
                        color = Color.Red
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Text(
                    text = "Version 1.0.0",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            item {
                if (showPopUp) {
                    AlertPopUp(
                        title = "Sign Out",
                        message = "Are you sure you want to sign out?",
                        confirmText = "Yes",
                        onDismiss = {
                            showPopUp = false
                        },
                        onSignOutSuccess = {
                            signUpViewModel.signOut(
                                onSignOutSuccess = onSignOutSuccess,
                                context = context
                            )

                            onDismiss()


                        })
                }
            }
            item{
                if(helpSheetState.isVisible)
                HelpAndSupportPopUp(
                    onDismiss = { coroutineScope.launch {
                        helpSheetState.hide()
                    }
                   },
                    helpSheetState
                )
            }

        }
    }
}



@Composable
fun ModalBottomHeader(
    headerText: String="",
    onDismiss: () -> Unit) {
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

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = headerText,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        HorizontalDivider(thickness = 1.dp)
    }
}

@Composable
fun SettingsItem(
    leadingIcon: ImageVector,
    title: String,
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = title,
                tint = color
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = "Forward Icon"
        )
    }
}

@Composable
fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}



@Composable
fun AlertPopUp(
    title: String,
    message: String,
    confirmText: String,
    onDismiss: () -> Unit,
    onSignOutSuccess: () -> Unit,) {

    AlertDialog(
        title = { Text(text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error) },
        text = { Text(text = message) },
        onDismissRequest =  onDismiss , confirmButton = {
            Text(text = confirmText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.clickable {
                    onDismiss()
                    onSignOutSuccess()
                })
        },
        dismissButton = {
            Text(text = "Cancel",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { onDismiss() }
                    .padding(end = 20.dp)
            )
        },


        )

}

private fun isLocationPermissionGranted(context: Context): Boolean {
    return if (ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            1
        )
        false
    } else {
        true
    }
}



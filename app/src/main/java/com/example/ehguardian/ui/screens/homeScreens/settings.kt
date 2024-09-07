package com.example.ehguardian.ui.screens.homeScreens

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ehguardian.data.repositories.FirebaseUserRepository
import com.example.ehguardian.data.services.Authentication
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



    ModalBottomSheet(
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.medium,
        sheetState = sheetState,
        dragHandle = {
            SettingsHeader(onDismiss = onDismiss)
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.95f),
            verticalArrangement = Arrangement.Center,
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
                       onClick = { /*TODO*/ }
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
                       onClick = { /*TODO*/ }
                   )
                   SettingsDivider()
                   SettingsItem(
                       leadingIcon = Icons.Filled.Language,
                       title = "Change Language",
                       onClick = { /*TODO*/ },
                   )
               }
           }

            item{Spacer(modifier = Modifier.height(16.dp))}

            // Second Card Section

            item{Card(
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

            }}
            item{Spacer(modifier = Modifier.height(16.dp))}

            item{Card(
                modifier = Modifier.fillMaxWidth(),

            ) {
                SettingsItem(
                    leadingIcon = Icons.AutoMirrored.Filled.Logout,
                    title = "Logout",
                    onClick = {

                            signUpViewModel.signOut(
                                onSignOutSuccess = onSignOutSuccess

                            )

                        onDismiss()
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
            }}
            item{Spacer(modifier = Modifier.height(16.dp))}
            item{Text(text = "Version 1.0.0",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,)}
        }
    }
}

@Composable
fun SettingsHeader(onDismiss: () -> Unit) {
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
                    text = "SETTINGS",
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
            .padding(16.dp)
            .clickable { onClick() }
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

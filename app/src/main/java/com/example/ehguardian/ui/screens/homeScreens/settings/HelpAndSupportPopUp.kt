package com.example.ehguardian.ui.screens.homeScreens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ehguardian.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpAndSupportPopUp(
    onDismiss: () -> Unit,
    sheetState: SheetState
) {
    val context = LocalContext.current

    ModalBottomSheet(
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = {
            ModalBottomHeader(
                headerText = "Help & Support",
                onDismiss = onDismiss
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp), // Distribute content evenly
        ) {
            // Email contact row
            ClickableRow(
                icon = Icons.Filled.Mail,
                text = "Contact us via email",
                onClick = {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_SENDTO,
                            Uri.parse("mailto:trondarius13@gmail.com")
                        )
                    )
                }
            )

        Spacer(modifier = Modifier.height(20.dp))
            // LinkedIn contact row
            ClickableRow(
                imageResId = R.drawable.linkedin,
                text = "Connect on LinkedIn",
                onClick = {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.linkedin.com/in/twumasi-ankrah-darius")
                        )
                    )
                }
            )
        }
    }
}

@Composable
fun ClickableRow(
    icon: ImageVector? = null,
    imageResId: Int? = null,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        imageResId?.let {
            Image(
                painter = painterResource(id = it),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }

        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(imageVector = Icons.Outlined.Link, contentDescription = null)
    }
}

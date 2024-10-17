package com.trontech.ehguardian.ui.screens.homeScreens.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Directions
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.trontech.ehguardian.R
import com.trontech.ehguardian.ui.AppViewModelProvider
import com.trontech.ehguardian.ui.screens.homeScreens.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyHospitals(
    onDismiss: () -> Unit,
    homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
     sheetState : SheetState = rememberModalBottomSheetState()

) {

    val hospitalList by homeViewModel.hospitals.observeAsState(emptyList())
    val context = LocalContext.current
    val isLoading by homeViewModel.isLoading.observeAsState(false)

    LaunchedEffect(Unit) {
        homeViewModel.fetchNearbyHospitals(context = context)
    }



    ModalBottomSheet(
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetState = sheetState,
        dragHandle = { ModalBottomHeader(headerText = "Nearby Hospitals", onDismiss = onDismiss) }
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp).align(Alignment.CenterHorizontally).padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.onSurface,
                strokeWidth = 2.dp,

            )
        } else {
            if (hospitalList.isEmpty()) {
                Column(

                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    Image(
                        painter = painterResource(id = R.drawable.disconnected),
                        contentDescription = "No data image"
                    )

                    Text(text = "No nearby hospitals found. Check your internet connection and try again.",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp),)
                }
            }
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                items(hospitalList.size) { item ->
                    val hospital = hospitalList[item]
                    LocationInfo(
                        name = hospital.displayName.name,
                        address = hospital.address,
                        rating = when (hospital.rating) {
                            null -> "N/A"
                            else -> "${hospital.rating}/5.0"
                        },
                        businessStatus = hospital.businessStatus,
                        status = "OPEN",

                        phone = hospital.phone,
                        googleMapsUri = hospital.googleMapsUri,

                        )
                }
            }
        }
    }
}


@Composable
fun LocationInfo(
    name: String ,
    address: String,
    rating: String ,
    businessStatus: String,
    status: String,
    phone: String = "",
    googleMapsUri: String = "",
    context: Context = LocalContext.current
){
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HospitalInfo(
            name =name,
            address = address,
            rating = rating,
            distance = businessStatus,
            status = status
        )
        ActionButtons(
            phone = phone,
            googleMapsUri = googleMapsUri,
            context = context
        )
        HorizontalDivider(

        )
    }


}


@Composable
fun HospitalInfo(
    name: String,
    address: String,
    rating: String,
    distance: String,
    status: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
            )
            Box(
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    text = rating,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 0.dp),
            horizontalArrangement = Arrangement.Start,
        ) {
            Image(
                painter = painterResource(id = R.drawable.map),
                contentDescription = "Location Icon",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = address,
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)),
                fontWeight = FontWeight.Bold
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.walk),
                contentDescription = "Distance Icon",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = distance)
        }

        Text(
            text = status,
            color = Color.Green,
        )
    }
}

@Composable
fun ActionButtons(
    phone: String = "",
    googleMapsUri: String = "",
    context: Context = LocalContext.current
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        ActionButtonItem(
            onClick = {
                context.startActivity(
                    Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse("tel:$phone")
                    )
                )
            },
            icon = Icons.Outlined.Phone,
            text = "Call"
        )
        ActionButtonItem(
            onClick = {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(googleMapsUri)
                    )
                )

            },
            icon = Icons.Outlined.Directions,
            text = "Directions"
        )
        ActionButtonItem(
            onClick = {
                context.startActivity(
                    Intent(
                        Intent.ACTION_SEND
                    ).apply {
                        type =  "text/plain"
                        putExtra(Intent.EXTRA_TEXT, googleMapsUri)
                        putExtra(Intent.EXTRA_SUBJECT, "Share Hospital Location")
                    }
                )
            },
            icon = Icons.Outlined.Share,
            text = "Share"
        )
    }
}

@Composable
fun ActionButtonItem(onClick: () -> Unit, icon: ImageVector, text: String) {
    TextButton(
        modifier = Modifier.background(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(12.dp)
        ),
        onClick = { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "$text Icon"
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

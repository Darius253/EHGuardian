package com.example.ehguardian.ui.screens.homeScreens.healthDataScreen



import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.ehguardian.R
import com.example.ehguardian.ui.AppViewModelProvider
import com.example.ehguardian.ui.screens.authenticationScreens.ToggleScreenButton
import com.example.ehguardian.ui.screens.homeScreens.HomeViewModel
import com.example.ehguardian.ui.screens.homeScreens.ModalBottomHeader


@Composable
fun HealthDataScreen(
    modifier: Modifier,
    homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var isHistory by remember { mutableStateOf(true) }
    val userMeasurements by homeViewModel.userMeasurements.collectAsState()
    var showPopUp by remember { mutableStateOf(false) }
    var requestHealthCheck by remember { mutableStateOf(false) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.heart)
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        speed = 1f,
        restartOnPlay = false
    )


    if (userMeasurements.isNotEmpty()) {
        Box(
            modifier= Modifier.fillMaxSize(),
        ) {
            Column {
                ToggleScreenButton(
                    isLogin = isHistory,
                    onButtonClick = { isHistory = !isHistory },
                    firstText = "History",
                    secondText = "Chart",
                    color = MaterialTheme.colorScheme.primaryContainer,

                    )

                if (isHistory) {
                    HistoryPage(
                        modifier = modifier,
                        userMeasurements = userMeasurements
                    )
                } else {
                    ChartPage(
                        modifier = modifier,
                        userMeasurements = userMeasurements
                    )
                }

            }
        }
        FloatingActionButton(
            onClick = {
                showPopUp = true
            },
            modifier = Modifier
                .padding(start = 325.dp, top = 590.dp),

        ) {

            LottieAnimation(
                composition,
                progress,
                modifier = Modifier.size(40.dp)
            )

        }
        if (showPopUp) {
            HealthCheckPopUp(onDismiss = {
                showPopUp = false
                requestHealthCheck = false },
                onButtonClick = {
                    requestHealthCheck = true
                },
                requestHealthCheck = requestHealthCheck
            )
        }
    }
    else{
        Column(
            modifier= Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.folder),
                contentDescription = "No Data Image",
                modifier = Modifier.size(200.dp)

            )
            Text(text = "No Data Available",
                style = MaterialTheme.typography.headlineMedium)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthCheckPopUp(
    onDismiss: () -> Unit,
    onButtonClick: () -> Unit,
    requestHealthCheck: Boolean,
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(12.dp),
        dragHandle = {
            ModalBottomHeader(
                headerText = "Health Check",
                onDismiss = onDismiss
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(
                0.95f
            ),
    ) {
        if (!requestHealthCheck) {
            // Show this when the button to take health check is not pressed
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Health Check",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onButtonClick, // This will update the requestHealthCheck state
                ) {
                    Text(text = "Take Health Check")
                }
            }
        } else {
            // Show this when requestHealthCheck is true (after button click)
           LazyColumn {
               item {
                   WebViewPage()
               }

           }
        }
    }
}



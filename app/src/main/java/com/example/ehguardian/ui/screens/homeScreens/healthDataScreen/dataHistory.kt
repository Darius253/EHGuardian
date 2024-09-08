package com.example.ehguardian.ui.screens.homeScreens.healthDataScreen



import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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


@Composable
fun HealthDataScreen(
    modifier: Modifier,
    homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var isHistory by remember { mutableStateOf(true) }
    val userMeasurements by homeViewModel.userMeasurements.collectAsState()

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
            Column(
                modifier = modifier.fillMaxSize(),
            ) {
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
                    BloodPressureGraph(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp),  // Set the height of the graph
                        userMeasurements = userMeasurements
                    )
                }

            }
        }
        FloatingActionButton(
            onClick = {

            },
            modifier = Modifier
                .padding(start = 325.dp, bottom = 10.dp, top = 650.dp),
            containerColor = MaterialTheme.colorScheme.primary,
        ) {

            LottieAnimation(
                composition,
                progress,
                modifier = Modifier.size(40.dp)
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

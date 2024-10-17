package com.example.ehguardian.ui.screens.authenticationScreens




import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.ehguardian.R
import com.example.ehguardian.ui.screens.authenticationScreens.login.LoginScreen
import com.example.ehguardian.ui.screens.authenticationScreens.signUp.SignUpScreen

@Composable
fun AuthenticationScreen(
    modifier: Modifier = Modifier,
    onForgotPasswordClick: () -> Unit,
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit
    ) {
    var isLogin by remember { mutableStateOf(true) }

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

    Column(
        modifier = modifier
            .padding(top = 25.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition,
            progress,
            modifier = Modifier.size(200.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
            colors = CardDefaults.cardColors()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ToggleScreenButton(isFirstPage = isLogin,
                  onButtonClick =  { isLogin = !isLogin},
                    firstText = "Login",
                    secondText = "Sign Up",
                    color = Color.White)



            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 5.dp)
            ) {


                item {
                    if (isLogin) {
                        LoginScreen(onForgotPasswordClick = onForgotPasswordClick, onSignInClick = onSignInClick)
                    } else {
                        SignUpScreen(onSignUpClick = onSignUpClick)
                    }
                }
            }
        }
    }
}
    }


@Composable
fun ToggleScreenButton(isFirstPage: Boolean, onButtonClick: () -> Unit, firstText: String, secondText: String, color: Color) {
    val boxOffset by animateDpAsState(
        targetValue = if (isFirstPage) 0.dp else 180.dp,
        label = "boxOffset",
        animationSpec = tween(durationMillis = 600)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .offset(x = boxOffset)
                    .padding(end = 10.dp)
                    .fillMaxWidth(0.5f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onButtonClick) {
                    Text(
                        text = firstText,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.W600,
                            color = if (isFirstPage) MaterialTheme.colorScheme.onPrimary else Color.Black
                        )
                    )
                }

                TextButton(onClick = onButtonClick) {
                    Text(
                        text = secondText,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.W600,
                            color = if (isFirstPage) Color.Black else MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }
    }
}

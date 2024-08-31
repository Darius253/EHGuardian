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
                .clip(RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp)),
            colors = CardDefaults.cardColors()
        ) {
            Column {
                AuthenticationButton(isLogin = isLogin) {
                    isLogin = !isLogin
                }


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
fun AuthenticationButton(isLogin: Boolean, onButtonClick: () -> Unit) {
    val boxOffset by animateDpAsState(
        targetValue = if (isLogin) 0.dp else 180.dp,
        label = "boxOffset",
        animationSpec = tween(durationMillis = 900)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(50))
            .background(Color.White),
        horizontalArrangement = Arrangement.SpaceAround,
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
                    .size(width = 150.dp, height = 40.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primary)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onButtonClick) {
                    Text(
                        text = "Log In",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.W600,
                            color = if (isLogin) MaterialTheme.colorScheme.onPrimary else Color.Black
                        )
                    )
                }

                TextButton(onClick = onButtonClick) {
                    Text(
                        text = "Sign Up",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.W600,
                            color = if (isLogin) Color.Black else MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }
    }
}

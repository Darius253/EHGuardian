package com.example.ehguardian.ui.screens.authenticationScreens



import android.content.res.Resources.Theme
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ehguardian.ui.screens.authenticationScreens.login.LoginScreen
import com.example.ehguardian.ui.screens.authenticationScreens.signUp.SignUpScreen


@Composable
fun AuthenticationScreen(modifier: Modifier = Modifier) {
    var isLogin by remember { mutableStateOf(true) }


    Column(
        modifier = modifier.padding(top = 25.dp).fillMaxSize(),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        Text(text = "Welcome to EHGuardian")
//        Spacer(modifier = Modifier.padding(60.dp))

        Card(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(
//                containerColor = MaterialTheme.colorScheme.primary,


            )
        ) {
            Column {
                AuthenticationButton(isLogin = isLogin) {
                    isLogin= !isLogin

                }
                if (isLogin) {
                    LoginScreen(onForgotPasswordClick = { /*TODO*/ }) {
                        isLogin = !isLogin

                    }
                }
                else{
                    SignUpScreen()
                }
            }
            // Row with a background and rounded corners
           }
    }
}

@Composable
fun AuthenticationButton(isLogin: Boolean, onButtonClick: () -> Unit){
    val boxOffset = animateDpAsState(
            targetValue = if (isLogin) 0.dp else 180.dp,
    animationSpec = tween(durationMillis = 900), label = ""
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
        Box(modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()) {
            // Animated box that moves under the text buttons
            Box(
                modifier = Modifier

                    .offset(x = boxOffset.value)
                    .size(width = 150.dp, height = 40.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primary)

            )

            // Text Buttons on top of the animated box
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
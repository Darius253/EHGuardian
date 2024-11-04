package com.trontech.ehguardian.ui.screens.authenticationScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.trontech.ehguardian.R
import com.trontech.ehguardian.ui.AppViewModelProvider
import com.trontech.ehguardian.ui.screens.authenticationScreens.login.LoginViewModel
import com.trontech.ehguardian.ui.screens.homeScreens.profile.InputField

@Composable


fun ForgotPasswordScreen(
    loginViewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)

) {
    var email by loginViewModel.email
    val isLoading by loginViewModel.isLoading.observeAsState(false)


    LazyColumn(
        modifier = Modifier.padding(16.dp),
    ) {
        item {
            Text(
                text = "Forgot \nPassword?",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        item {
            Image(
                painter = painterResource(id = R.drawable.wrong_password),
                contentDescription = "Forgot Password",
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.3f)
                    .padding(top = 16.dp)
            )
        }
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }

        item {
            Text(
                text = "Please enter your email address and we will send you a link to reset your password.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            )
        }

        item { Spacer(modifier = Modifier.height(25.dp)) }

        item {
            InputField(
                label = "Enter your Email",
                value = email,
                onValueChange = { email = it },
                keyboardType = KeyboardType.Email,
                onDone = {
                },
            )
        }
        if (loginViewModel.resetPasswordResult.value == false) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                        disabledContentColor = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = "Back Icon",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(text = "Sorry, we can't find an account with this email address. Kindly enter your email address correctly.")
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(25.dp))
        }

        if (isLoading) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp,

                        )
                }
            }
        } else {
            if (loginViewModel.email.value.isNotEmpty()) {
                item {
                    Button(
                        onClick = {
                            loginViewModel.resetPassword()

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Reset Password",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W600),
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                item {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onSurface

                        ),
                        border = ButtonDefaults.outlinedButtonBorder,
                        onClick = {


                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Back Icon",
                                tint = MaterialTheme.colorScheme.onSurface

                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Back to Login",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W600),
                            )
                        }
                    }
                }
            }
        }
    }
}



package com.example.ehguardian.ui.screens.authenticationScreens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onForgotPasswordClick: () -> Unit, // Add callback for Forgot Password
    onSignUpClick: () -> Unit  // Add callback for Sign Up
) {
    Column(
        modifier = modifier
            .padding(25.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Welcome Text
        Text(
            text = "Welcome Back!",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.W600
            )
        )
        Text(
            text = "Please sign in to continue",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.padding(16.dp))

        // Email Field
        EmailTextField()
        Spacer(modifier = Modifier.padding(8.dp))

        // Password Field
        PasswordTextField()
        Spacer(modifier = Modifier.padding(8.dp))

        // Sign In Button
        Button(
            onClick = { /* Handle login logic */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Sign In",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W600)
            )
        }

        // Forgot Password
        Spacer(modifier = Modifier.padding(3.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onForgotPasswordClick, // Use the callback
                modifier = Modifier.padding(end = 10.dp)
            ) {
                Text(
                    text = "Forgot Password?",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.W600,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        // Sign Up
        Spacer(modifier = Modifier.padding(30.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Don't have an account yet? ")
            TextButton(
                onClick = onSignUpClick
            ) {
               Text( text = "Sign Up here!",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.W600,
                    color = MaterialTheme.colorScheme.primary
                ),
               )
                // Make "Sign Up here!" clickable
            }
        }
    }
}

@Composable
fun EmailTextField() {
    var email by rememberSaveable { mutableStateOf("") }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        maxLines = 1,
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Email,
                contentDescription = "Email Icon"
            )
        },
        value = email,
        label = { Text(text = "Email") },
        onValueChange = { email = it },
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
    )
}

@Composable
fun PasswordTextField() {
    var password by rememberSaveable { mutableStateOf("") }
    var isVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = "Password Icon"
            )
        },

        trailingIcon = {
            if (isVisible)
            TextButton(onClick = { isVisible = !isVisible }) {
               Text( text = "Hide Password")
            }
            else
                TextButton(onClick = { isVisible = true }) {
                    Text( text = "Show Password")
                }

        },
        value = password,

        label = { Text(text = "Password") },
        maxLines = 1,

        onValueChange = { password = it },
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),

    )
}
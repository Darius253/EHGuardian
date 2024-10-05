package com.example.ehguardian.ui.screens.authenticationScreens.login


import android.util.Patterns.EMAIL_ADDRESS
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.ehguardian.ui.AppViewModelProvider

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onSignInClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
     loginViewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val email by loginViewModel.email
    val password by loginViewModel.password
    val isLoading by loginViewModel.isLoading.observeAsState()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .padding(20.dp)
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
        Spacer(modifier = Modifier.height(16.dp))

        // Email Field
        EmailTextField(
            email = email,
            onEmailChange = loginViewModel::onEmailChange,

        )
        Spacer(modifier = Modifier.height(8.dp))

        // Password Field
        PasswordTextField(
            password = password,
            onPasswordChange = loginViewModel::onPasswordChange,
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Sign In Button

        if(isLoading == true)
        Button(
            onClick = { loginViewModel.signIn(
                context = context,
                onSignInSuccess = {
                    onSignInClick()

                }

            )


                      },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)

        ) {
            Text(
                text = "Sign In",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W600),

            )
        }
        else CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = MaterialTheme.colorScheme.onSurface,
            strokeWidth = 2.dp
        )

        // Forgot Password
        Spacer(modifier = Modifier.height(3.dp))

        TextButton(
            onClick = onForgotPasswordClick // Use the callback
        ) {
            Text(
                text = "Forgot Password? Tap Here!",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.W600,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

@Composable
fun EmailTextField(email: String, onEmailChange: (String) -> Unit ) {


    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = email,
        onValueChange = onEmailChange,
        maxLines = 1,
        singleLine = true,
        label = { Text(text = "Email") },
        supportingText = {
        if(email.isNotEmpty() && !EMAIL_ADDRESS.matcher(email).matches() )
            Text("Email must be valid", color = MaterialTheme.colorScheme.error)

            else if(email.isEmpty())
                Text("This field is required", color = MaterialTheme.colorScheme.error)

        },

        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Email,
                contentDescription = "Email Icon"
            )
        },
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
    )
}

@Composable
fun PasswordTextField(
    password: String,
    onPasswordChange: (String) -> Unit,
) {
    var isVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = password,
        onValueChange = onPasswordChange,
        singleLine = true,
        maxLines = 1,
        supportingText = {
            if(password.isNotEmpty() && password.length < 8)
                Text("Password must be at least 8 characters long", color = MaterialTheme.colorScheme.error)
            else if(password.isEmpty())
                Text("This field is required", color = MaterialTheme.colorScheme.error)
        },
        label = { Text(text = "Password") },
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = "Password Icon"
            )
        },
        trailingIcon = {
            IconButton(onClick = { isVisible = !isVisible }) {
                Icon(
                    imageVector = if (isVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                    contentDescription = if (isVisible) "Hide Password" else "Show Password"
                )
            }
        },
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
}

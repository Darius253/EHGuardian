package com.example.ehguardian.ui.screens.authenticationScreens.signUp

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ehguardian.ui.screens.authenticationScreens.login.EmailTextField
import com.example.ehguardian.ui.screens.authenticationScreens.login.PasswordTextField

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    onSignUpClick: () -> Unit,
    signUpViewModel: SignUpViewModel = viewModel(),
    ) {

    var expanded by rememberSaveable { mutableStateOf(false) }


    val email by signUpViewModel.email.observeAsState("")
    val password by signUpViewModel.password.observeAsState("")
    val signUpSuccess by signUpViewModel.signUpSuccess.observeAsState(false)
    val errorMessage by signUpViewModel.errorMessage.observeAsState(null)
    val firstName by signUpViewModel.name.observeAsState("")
    val lastName by signUpViewModel.surname.observeAsState("")
    val selectedGender by signUpViewModel.gender.observeAsState("")
    val weight by signUpViewModel.weight.observeAsState("")
    val height by signUpViewModel.height.observeAsState("")


    val context = LocalContext.current

    Column(
        modifier = modifier
            .padding(20.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Welcome Text
        Text(
            text = "Join Us!",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.W600)
        )
        Text(
            text = "Create an account to continue",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Name Fields
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(3f),
                value = firstName,
                onValueChange = { signUpViewModel.onNameChanged(it) },
                shape = RoundedCornerShape(12.dp),
                maxLines = 1,
                supportingText = {
                    if (errorMessage != null) {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                label = { Text("First Name", style = MaterialTheme.typography.titleMedium) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                modifier = Modifier.weight(3f),
                value = lastName,
                onValueChange = { signUpViewModel.onSurnameChanged(it) },
                shape = RoundedCornerShape(12.dp),
                maxLines = 1,
                supportingText = {
                    if (errorMessage != null) {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                label = { Text("Last Name", style = MaterialTheme.typography.titleMedium) }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Email and Password Fields
        EmailTextField(
            email = email,
            onEmailChange = signUpViewModel::onEmailChanged,
            errorMessage = errorMessage

        )
        Spacer(modifier = Modifier.height(8.dp))
        PasswordTextField(
            password = password,
            onPasswordChange = signUpViewModel::onPasswordChanged,
            errorMessage = errorMessage
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Gender Selection
        Box {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = selectedGender,
                supportingText = {
                    if (errorMessage != null) {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                },
                onValueChange = {
                    signUpViewModel.onGenderChanged(it)
                },
                readOnly = true,
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Outlined.ArrowDropDown, contentDescription = "Select Gender")
                    }
                },
                label = { Text("Gender", style = MaterialTheme.typography.titleMedium) }
            )
            DropdownMenu(
                modifier = Modifier.width(350.dp),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Male", style = MaterialTheme.typography.titleMedium) },
                    onClick = {
                        signUpViewModel.onGenderChanged("Male")
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Female", style = MaterialTheme.typography.titleMedium) },
                    onClick = {
                       signUpViewModel.onGenderChanged("Female")
                        expanded = false
                    }
                )


            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(3f),
                value = weight,
                onValueChange = {
                    signUpViewModel.onWeightChanged(weight)
                },
                shape = RoundedCornerShape(12.dp),
                maxLines = 1,
                supportingText = {
                    if (errorMessage != null) {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Weight in kg", style = MaterialTheme.typography.titleMedium) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                modifier = Modifier.weight(3f),
                value = height,
                onValueChange = {
                    signUpViewModel.onHeightChanged(height)
                },
                shape = RoundedCornerShape(12.dp),
                maxLines = 1,
                supportingText = {
                    if (errorMessage != null) {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Height in ft", style = MaterialTheme.typography.titleMedium) }
            )
        }
        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Sign Up Button
        Button(
            onClick = signUpViewModel::signUp,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W600)
            )
        }
    }
}

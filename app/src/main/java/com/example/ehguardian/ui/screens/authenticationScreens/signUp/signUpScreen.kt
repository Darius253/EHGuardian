package com.example.ehguardian.ui.screens.authenticationScreens.signUp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ehguardian.ui.screens.authenticationScreens.login.EmailTextField
import com.example.ehguardian.ui.screens.authenticationScreens.login.PasswordTextField

@Composable
fun SignUpScreen(modifier: Modifier = Modifier, onSignUpClick: () -> Unit ) {
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var weight by rememberSaveable { mutableStateOf("") }
    var height by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedGender by rememberSaveable { mutableStateOf("Select Gender") }

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
                onValueChange = { firstName = it },
                shape = RoundedCornerShape(12.dp),
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                label = { Text("First Name", style = MaterialTheme.typography.titleMedium) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                modifier = Modifier.weight(3f),
                value = lastName,
                onValueChange = { lastName = it },
                shape = RoundedCornerShape(12.dp),
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                label = { Text("Last Name", style = MaterialTheme.typography.titleMedium) }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Email and Password Fields
        EmailTextField()
        Spacer(modifier = Modifier.height(8.dp))
        PasswordTextField()
        Spacer(modifier = Modifier.height(8.dp))

        // Gender Selection
        Box {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = selectedGender,
                onValueChange = {},
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
                        selectedGender = "Male"
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Female", style = MaterialTheme.typography.titleMedium) },
                    onClick = {
                        selectedGender = "Female"
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
                onValueChange = { weight = it },
                shape = RoundedCornerShape(12.dp),
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Weight in kg", style = MaterialTheme.typography.titleMedium) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                modifier = Modifier.weight(3f),
                value = height,
                onValueChange = { height = it },
                shape = RoundedCornerShape(12.dp),
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Height in ft", style = MaterialTheme.typography.titleMedium) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign Up Button
        Button(
            onClick = onSignUpClick,
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

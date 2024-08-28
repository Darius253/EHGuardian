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
fun SignUpScreen(modifier: Modifier = Modifier) {
    var firstName by rememberSaveable {
        mutableStateOf("")
    }
    var lastname by rememberSaveable {
        mutableStateOf("")
    }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedGender by rememberSaveable { mutableStateOf("Select Gender") } // Store selected gender

    Column(
        modifier = modifier
            .padding(25.dp)
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
        Spacer(modifier = Modifier.padding(16.dp))

        // Name Fields
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(3f),
                value = firstName, // Use state to manage input
                onValueChange = { firstName = it },
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                label = { Text("First Name", style = MaterialTheme.typography.titleMedium) }
            )
            Spacer(modifier = Modifier.padding(8.dp))
            OutlinedTextField(
                modifier = Modifier.weight(3f),
                value = lastname, // Use state to manage input
                onValueChange = { lastname = it },
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                label = { Text("Last Name", style = MaterialTheme.typography.titleMedium) }
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))

        // Email and Password Fields
        EmailTextField()
        Spacer(modifier = Modifier.padding(8.dp))
        PasswordTextField()
        Spacer(modifier = Modifier.padding(8.dp))

        // Gender Selection
        Box {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = selectedGender,
                onValueChange = {}, // Prevent direct input
                readOnly = true,
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Outlined.ArrowDropDown, contentDescription = "Select Gender")
                    }
                },
                label = { Text("Gender", style = MaterialTheme.typography.titleMedium)

                }
            )
            DropdownMenu(modifier= Modifier
                .width(350.dp),expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text("Male", style =
                        MaterialTheme.typography.titleMedium) },
                    onClick = {
                        selectedGender = "Male"
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Female", style= MaterialTheme.typography.titleMedium) },
                    onClick = {
                        selectedGender = "Female"
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Other", style= MaterialTheme.typography.titleMedium) },
                    onClick = {
                        selectedGender = "Other"
                        expanded = false
                    }
                )
            }
        }
        Spacer(modifier = Modifier.padding(8.dp))


    }
}
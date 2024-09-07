package com.example.ehguardian.ui.screens.authenticationScreens.signUp

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
import com.example.ehguardian.data.models.MeasurementData
import com.example.ehguardian.data.models.UserModel
import com.example.ehguardian.ui.AppViewModelProvider
import com.example.ehguardian.ui.screens.authenticationScreens.login.EmailTextField
import com.example.ehguardian.ui.screens.authenticationScreens.login.PasswordTextField
import com.example.ehguardian.ui.screens.homeScreens.profile.DateOfBirthInputField
import com.example.ehguardian.ui.screens.homeScreens.profile.DatePickerModal
import java.time.LocalDateTime
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    onSignUpClick: () -> Unit,
    signUpViewModel: SignUpViewModel = viewModel(factory = AppViewModelProvider.Factory),
    ) {

    var expanded by rememberSaveable { mutableStateOf(false) }

    val id = signUpViewModel.id
    val email by signUpViewModel.email.observeAsState("")
    val password by signUpViewModel.password.observeAsState("")
    val errorMessage by signUpViewModel.errorMessage.observeAsState(null)
    val firstName by signUpViewModel.firstname.observeAsState("")
    val lastName by signUpViewModel.surname.observeAsState("")
    val selectedGender by signUpViewModel.gender.observeAsState("")
    val userWeight by signUpViewModel.weight.observeAsState("")
    val userHeight by signUpViewModel.height.observeAsState("")
    val dateOfBirth by signUpViewModel.dateOfBirth.observeAsState("")
    val isLoading by signUpViewModel.isLoading.observeAsState(false)
    var showCalendar by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()




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
                    errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
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
                    errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                label = { Text("Last Name", style = MaterialTheme.typography.titleMedium) }
            )
        }

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
                    errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
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
                value = userWeight,
                onValueChange = {
                    signUpViewModel.onWeightChanged(it)
                },
                shape = RoundedCornerShape(12.dp),
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Weight in kg", style = MaterialTheme.typography.titleMedium) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                modifier = Modifier.weight(3f),
                value = userHeight,
                onValueChange = {
                    signUpViewModel.onHeightChanged(it)
                },
                shape = RoundedCornerShape(12.dp),
                maxLines = 1,

                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Height in ft", style = MaterialTheme.typography.titleMedium) }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        DateOfBirthInputField(
            label = "Date of Birth",
            dateOfBirth = dateOfBirth.toString(),
            showCalendar = showCalendar,
            onCalendarToggle = { showCalendar = it },
            onValueChange = {
                signUpViewModel.onDateOfBirthChanged(Date(it))
            }



            )
        if (showCalendar) {
            DatePickerModal(
                onDateSelected = {
                    showCalendar = false
                    datePickerState.selectedDateMillis?.let {
                        signUpViewModel.onDateOfBirthChanged(Date(it))
                    }


                },
                onDismiss = { showCalendar = false },
                datePickerState = datePickerState,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign Up Button
        if (!isLoading)
        Button(
            onClick = {signUpViewModel.signUp(user = UserModel(
                id = id,
                email = email,
                password = password,
                firstname = firstName,
                lastname = lastName,
                gender = selectedGender,
                userWeight = userWeight,
                userHeight = userHeight,
                dateOfBirth = dateOfBirth,
                createdDate = LocalDateTime.now().year,
                measurementData = MeasurementData()

            ),
                context = context,
                      onSignUpSuccess = onSignUpClick
            )},
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
        else CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = MaterialTheme.colorScheme.onSurface,
            strokeWidth = 2.dp
        )
    }
}

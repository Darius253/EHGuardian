package com.trontech.ehguardian.ui.screens.authenticationScreens.signUp

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
import com.trontech.ehguardian.data.models.UserModel
import com.trontech.ehguardian.ui.AppViewModelProvider
import com.trontech.ehguardian.ui.screens.authenticationScreens.login.EmailTextField
import com.trontech.ehguardian.ui.screens.authenticationScreens.login.PasswordTextField
import com.trontech.ehguardian.ui.screens.homeScreens.profile.DateOfBirthInputField
import com.trontech.ehguardian.ui.screens.homeScreens.profile.DatePickerModal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    onSignUpClick: () -> Unit,
    signUpViewModel: SignUpViewModel = viewModel(factory = AppViewModelProvider.Factory),
    ) {

    var expanded by rememberSaveable { mutableStateOf(false) }


    val email by signUpViewModel.email.observeAsState("")
    val password by signUpViewModel.password.observeAsState("")
    val firstName by signUpViewModel.firstname.observeAsState("")
    val lastName by signUpViewModel.surname.observeAsState("")
    val selectedGender by signUpViewModel.gender.observeAsState("")
    val userWeight by signUpViewModel.weight.observeAsState("")
    val userHeight by signUpViewModel.height.observeAsState("")
    val dateOfBirth by signUpViewModel.dateOfBirth.observeAsState("")
    val isLoading by signUpViewModel.isLoading.observeAsState(false)
    var showCalendar by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val createdDate = LocalDateTime.now()

// Create a formatter to extract Year, Day of Year, and Time
    val formatter = DateTimeFormatter.ofPattern("yyyy-DDD HH:mm:ss")

// Format the createdDate to the required format
    val formattedDate = createdDate.format(formatter)




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
                   if(firstName.isEmpty()){
                       Text("This field is required", color = MaterialTheme.colorScheme.error)
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
                   if(lastName.isEmpty()){
                       Text("This field is required", color = MaterialTheme.colorScheme.error)
                   }
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

        )
        Spacer(modifier = Modifier.height(8.dp))
        PasswordTextField(
            password = password,
            onPasswordChange = signUpViewModel::onPasswordChanged,
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Gender Selection
        Box {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = selectedGender,
                supportingText = {
                    if(!selectedGender.contains("Male") && !selectedGender.contains("Female")) {
                        Text("This field is required", color = MaterialTheme.colorScheme.error)
                    }

                },
                onValueChange = {
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
                value = userWeight.toString(),
                onValueChange = {
                    signUpViewModel.onWeightChanged(it.toDouble())
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
                value = userHeight.toString(),
                onValueChange = {
                    signUpViewModel.onHeightChanged(it.toDouble())
                },
                shape = RoundedCornerShape(12.dp),
                maxLines = 1,

                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Height in metres", style = MaterialTheme.typography.titleMedium) }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        DateOfBirthInputField(
            label = "Date of Birth",
            dateOfBirth = dateOfBirth,
            showCalendar = showCalendar,
            onCalendarToggle = { showCalendar = it },
            onValueChange = {
                signUpViewModel.onDateOfBirthChanged(Date(it))
            }




            )
        if (dateOfBirth.isEmpty()){
            Text(
                text = "This field is required",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.error
            )
        }
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
                email = email,
                password = password,
                firstname = firstName,
                lastname = lastName,
                gender = selectedGender,
                userWeight = userWeight.toString(),
                userHeight = userHeight.toString(),
                dateOfBirth = dateOfBirth,
                createdDate = formattedDate,
                bloodSugarLevel = "",
                cholesterolLevel = "",
                userImage = "",
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

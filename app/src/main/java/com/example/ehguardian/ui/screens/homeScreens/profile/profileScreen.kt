package com.example.ehguardian.ui.screens.homeScreens.profile


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    var firstName by rememberSaveable { mutableStateOf("Darius") }
    var lastName by rememberSaveable { mutableStateOf("Twumasi-Ankrah") }
    var weight by rememberSaveable { mutableStateOf("") }
    var height by rememberSaveable { mutableStateOf("") }
    var selectedGender by rememberSaveable { mutableStateOf("Select Gender") }
    var cholesterolLevel by rememberSaveable { mutableStateOf("") }
    var bloodSugarLevel by rememberSaveable { mutableStateOf("") }
    var isGenderMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var showCalendar by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val dateOfBirth = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    LazyColumn(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item { ProfileImage() }
        item { Spacer(modifier = Modifier.height(50.dp)) }
        item { NameInputFields(firstName, lastName) { firstName = it.first; lastName = it.second } }
        item { Spacer(modifier = Modifier.height(10.dp)) }
        item {
            GenderDropdown(
                label = "Gender",
                selectedGender = selectedGender,
                isExpanded = isGenderMenuExpanded,
                onGenderSelected = { selectedGender = it },
                onExpandedChange = { isGenderMenuExpanded = it }
            )
        }
        item { Spacer(modifier = Modifier.height(10.dp)) }
        item {
            DateOfBirthInputField(
                label = "Date of Birth",
                dateOfBirth = dateOfBirth,
                showCalendar = showCalendar,
                onCalendarToggle = { showCalendar = it },
                onValueChange = {}

            )
        }
        item { Spacer(modifier = Modifier.height(10.dp)) }
        item {
            WeightAndHeightInputFields(
                weight = weight,
                height = height,
                onWeightChange = { weight = it },
                onHeightChange = { height = it }
            )
        }
        item { Spacer(modifier = Modifier.height(10.dp)) }
        item {
            CholesterolAndBloodSugarInputFields(
                cholesterolLevel = cholesterolLevel,
                bloodSugarLevel = bloodSugarLevel,
                onCholesterolChange = { cholesterolLevel = it },
                onBloodSugarChange = { bloodSugarLevel = it }
            )
        }
        item { Spacer(modifier = Modifier.height(50.dp)) }
        item {
            UpdateDetailsButton(onClick = { /*TODO*/ })
        }
    }

    if (showCalendar) {
        DatePickerModal(
            onDateSelected = {
                showCalendar = false
                datePickerState.selectedDateMillis?.let {
                    datePickerState.selectedDateMillis = it
                }
            },
            onDismiss = { showCalendar = false },
            datePickerState = datePickerState,
        )
    }
}

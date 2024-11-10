package com.trontech.ehguardian.ui.screens.homeScreens.profile



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.trontech.ehguardian.ui.AppViewModelProvider
import com.trontech.ehguardian.ui.screens.homeScreens.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val userDetails by homeViewModel.userDetails.collectAsState()
    val context = LocalContext.current

    // Get a reference to the FocusManager
    val focusManager: FocusManager = LocalFocusManager.current

    // Ensure we have user details
    if (userDetails != null) {
        // Initial values (from the userDetails)
        val initialFirstName = userDetails!!.firstname
        val initialLastName = userDetails!!.lastname
        val initialWeight = userDetails!!.userWeight
        val initialHeight = userDetails!!.userHeight
        val initialGender = userDetails!!.gender
        val initialCholesterolLevel = userDetails!!.cholesterolLevel
        val initialBloodSugarLevel = userDetails!!.bloodSugarLevel
        val initialDateOfBirth = userDetails!!.dateOfBirth
        val initialImage = userDetails!!.userImage

        // Current editable values
        var firstName by rememberSaveable { mutableStateOf(initialFirstName) }
        var lastName by rememberSaveable { mutableStateOf(initialLastName) }
        var weight by rememberSaveable { mutableStateOf(initialWeight) }
        var height by rememberSaveable { mutableStateOf(initialHeight) }
        var selectedGender by rememberSaveable { mutableStateOf(initialGender) }
        var cholesterolLevel by rememberSaveable { mutableStateOf(initialCholesterolLevel) }
        var bloodSugarLevel by rememberSaveable { mutableStateOf(initialBloodSugarLevel) }
        var selectedImage by rememberSaveable { mutableStateOf(initialImage) }
        var isGenderMenuExpanded by rememberSaveable { mutableStateOf(false) }
        var showCalendar by rememberSaveable { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState()
        var dateOfBirth by rememberSaveable { mutableStateOf(initialDateOfBirth) }



        val isLoaded by homeViewModel.isLoading.observeAsState(false)

        // Compare current values with initial values
        val isChanged = firstName != initialFirstName ||
                lastName != initialLastName ||
                weight != initialWeight ||
                height != initialHeight ||
                selectedGender != initialGender ||
                cholesterolLevel != initialCholesterolLevel ||
                bloodSugarLevel != initialBloodSugarLevel ||
                dateOfBirth != initialDateOfBirth || selectedImage != initialImage

        Box {

            LazyColumn(
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {


                item {
                    ProfileImage(
                        selectedImage = selectedImage,
                        onImageSelected = {
                            selectedImage = it
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(50.dp)) }
                item {
                    NameInputFields(
                        firstName = firstName,
                        lastName = lastName,
                        onNameChange = { (newFirstName, newLastName) ->
                            firstName = newFirstName
                            lastName = newLastName
                        },
                        onDone = { focusManager.clearFocus() } // Clear focus when "Done" is clicked
                    )
                }
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
                        dateOfBirth = dateOfBirth.toString(),
                        showCalendar = showCalendar,
                        onCalendarToggle = { showCalendar = it },
                        onValueChange = { newDateOfBirth ->
                            dateOfBirth = newDateOfBirth
                        },
                        // Clear focus after selecting date
                    )
                }
                item { Spacer(modifier = Modifier.height(10.dp)) }
                item {
                    WeightAndHeightInputFields(
                        weight = weight,
                        height = height,
                        onWeightChange = { weight = it },
                        onHeightChange = { height = it },
                        onDone = { focusManager.clearFocus() } // Clear focus when "Done" is clicked
                    )
                }
                item { Spacer(modifier = Modifier.height(10.dp)) }
                item {
                    CholesterolAndBloodSugarInputFields(
                        cholesterolLevel = cholesterolLevel,
                        bloodSugarLevel = bloodSugarLevel,
                        onCholesterolChange = { cholesterolLevel = it },
                        onBloodSugarChange = { bloodSugarLevel = it },
                        onDone = { focusManager.clearFocus() } // Clear focus when "Done" is clicked
                    )
                }
                item { Spacer(modifier = Modifier.height(50.dp)) }

                // Conditionally show the "Update" button only if any field is changed
                if (isChanged) {
                    item {
                        if (isLoaded) {
                            CircularProgressIndicator()
                        } else {
                            UpdateDetailsButton(onClick = {
                                userDetails?.let {
                                    homeViewModel.updateUserDetails(
                                        it.copy(
                                            firstname = firstName,
                                            lastname = lastName,
                                            gender = selectedGender,
                                            userWeight = weight,
                                            userHeight = height,
                                            cholesterolLevel = cholesterolLevel,
                                            bloodSugarLevel = bloodSugarLevel,
                                            dateOfBirth = dateOfBirth,
                                            userImage = selectedImage,
                                        ),
                                        context,
                                    )
                                }
                            })
                        }
                    }
                }
            }
        }

        if (showCalendar) {

                    DatePickerModal(
                        onDateSelected = { selectedDateMillis ->
                            showCalendar = false
                            selectedDateMillis?.let {
                                // Convert the selected date in milliseconds to a formatted date string
                                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                val formattedDate = formatter.format(it) // Convert milliseconds to a Date, then format
                                dateOfBirth = formattedDate // Set the formatted date to dateOfBirth
                            }
                        },
                        onDismiss = { showCalendar = false },
                        datePickerState = datePickerState,
                    )

        }

    }
}


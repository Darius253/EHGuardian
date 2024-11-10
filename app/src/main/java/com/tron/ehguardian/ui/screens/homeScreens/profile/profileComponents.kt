package com.tron.ehguardian.ui.screens.homeScreens.profile



import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tron.ehguardian.R


@Composable
fun ProfileImage(
    selectedImage: String,
    onImageSelected: (String) -> Unit

)  {
    val context = LocalContext.current

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
        onImageSelected(uri.toString())

        } else {
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }


        Box {

            if (selectedImage.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(120.dp)
                ){

                   AsyncImage(model = selectedImage,
                        contentDescription ="Profile Image",
                        contentScale = ContentScale.Crop,
                        filterQuality = FilterQuality.Medium,
                    )


                }


            }else {

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(120.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(16.dp),
                        painter = painterResource(id = R.drawable.user),
                        contentDescription = "User Profile Image",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
                    )

                }
            }

            Image(
                painter = painterResource(id = R.drawable.camera),
                contentDescription = "Upload Image Button",
                modifier = Modifier
                    .clickable {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))


                    }
                    .size(35.dp)
                    .align(Alignment.BottomEnd)
            )
        }


}









@Composable
fun NameInputFields(
    firstName: String,
    lastName: String,
    onNameChange: (Pair<String, String>) -> Unit,
    onDone: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        InputField(
            label = "First Name",
            value = firstName,
            onValueChange = { onNameChange(Pair(it, lastName)) },
            modifier = Modifier.weight(1f),
            onDone = onDone
        )
        InputField(
            label = "Last Name",
            value = lastName,
            onValueChange = { onNameChange(Pair(firstName, it)) },
            modifier = Modifier.weight(1f),
            onDone = onDone
        )
    }
}





@Composable
fun GenderDropdown(
    label: String,
    selectedGender: String,
    isExpanded: Boolean,
    onGenderSelected: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = selectedGender,
            onValueChange = {},
            singleLine = true,
            maxLines = 1,
            readOnly = true,
            trailingIcon = {
                IconButton(
                    onClick = { onExpandedChange(!isExpanded) }
                ) {
                    Icon(Icons.Outlined.ArrowDropDown, contentDescription = "Select Gender")
                }
            },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = isExpanded,
            modifier = Modifier.fillMaxWidth(),
            onDismissRequest = { onExpandedChange(false) }
        ) {
            listOf("Male", "Female").forEach { gender ->
                DropdownMenuItem(
                    text = { Text(gender, style = MaterialTheme.typography.titleMedium) },
                    onClick = {
                        onGenderSelected(gender)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

@Composable
fun DateOfBirthInputField(
    label: String,
    dateOfBirth: String,
    showCalendar: Boolean,
    onCalendarToggle: (Boolean) -> Unit,
    onValueChange: (String) -> Unit,

    ) {
    Column{
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = dateOfBirth,
            onValueChange = onValueChange,
            singleLine = true,
            maxLines = 1,
            readOnly = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = "Calendar Icon"
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = { onCalendarToggle(!showCalendar) }
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit Icon")
                }
            },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .clickable(
                    onClick = {
                        onCalendarToggle(!showCalendar)
                    }
                )
                .fillMaxWidth()
        )
    }
}

@Composable
fun WeightAndHeightInputFields(
    weight: String,
    height: String,
    onWeightChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onDone: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        InputField(
            label = "Weight",
            value = weight,
            onValueChange = onWeightChange,
            modifier = Modifier.weight(1f),
            keyboardType = KeyboardType.Number,
            suffix = "kg",
            onDone = onDone
        )
        InputField(
            label = "Height",
            value = height,
            onValueChange = onHeightChange,
            modifier = Modifier.weight(1f),
            keyboardType = KeyboardType.Number,
            suffix = "metre",
            onDone = onDone
        )
    }
}

@Composable
fun CholesterolAndBloodSugarInputFields(
    cholesterolLevel: String,
    bloodSugarLevel: String,
    onCholesterolChange: (String) -> Unit,
    onBloodSugarChange: (String) -> Unit,
    onDone: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        InputField(
            label = "Cholesterol Level",
            value = cholesterolLevel,
            onValueChange = onCholesterolChange,
            modifier = Modifier.weight(1f),
            keyboardType = KeyboardType.Number,
            suffix = "mg/dL",
            onDone = onDone

            )
        InputField(
            label = "Blood Sugar Level",
            value = bloodSugarLevel,
            onValueChange = onBloodSugarChange,
            modifier = Modifier.weight(1f),
            keyboardType = KeyboardType.Number,
            suffix = "mg/dL",
            onDone = onDone
        )
    }
}

@Composable
fun UpdateDetailsButton(onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Text(
            text = "Update Details",
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Any?) -> Unit,
    onDismiss: () -> Unit,
    datePickerState: DatePickerState,
) {
    DatePickerDialog(
        shape = MaterialTheme.shapes.medium,
        onDismissRequest = onDismiss,
        colors = DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium),
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(
                    datePickerState.selectedDateMillis
                )
                onDismiss()
            }) {
                Text(
                    "OK",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.error)
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            headline = {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "Date of Birth",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        )
    }
}

@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    suffix: String = "",
    onDone: () -> Unit,
    placeholder:@Composable () -> Unit = {}
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = value,
            placeholder = placeholder,

            onValueChange = onValueChange,
            singleLine = true,
            maxLines = 1,
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth(),
            suffix = {
                if (suffix.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .width(60.dp)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.onSurface,
                                MaterialTheme.shapes.medium
                            )
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                        ,
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(5.dp),
                            text = suffix,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        )
    }
}



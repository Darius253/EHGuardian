package com.tron.ehguardian.ui.screens.homeScreens.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeLanguagePopUp(
    sheetState: SheetState,
    onDismissRequest: () -> Unit
) {
    var selectedLanguage by remember { mutableStateOf(Language.ENGLISH) }

    ModalBottomSheet(
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetState = sheetState,
        modifier = Modifier.fillMaxHeight(0.5F),
        dragHandle = {
            ModalBottomHeader(
                headerText = "Change Language",
                onDismiss = onDismissRequest,
            )
        }
    ) {
        LanguageSelectionList(
            selectedLanguage = selectedLanguage,
            onLanguageSelected = { selectedLanguage = it }
        )
    }
}

@Composable
fun LanguageSelectionList(
    selectedLanguage: Language,
    onLanguageSelected: (Language) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(Language.entries) { language ->
            LanguageOption(
                language = language,
                isSelected = selectedLanguage == language,
                onSelect = { onLanguageSelected(language) }
            )
        }
    }
}

@Composable
fun LanguageOption(
    language: Language,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onSelect)
            .border(
            shape = RoundedCornerShape(12.dp),
            width = 1.dp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        ),
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect
        )
        Text(
            text = language.displayName,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold
            ),

            color = MaterialTheme.colorScheme.onBackground,

        )
    }
}

enum class Language(val displayName: String) {
    ENGLISH("English"),
//    SPANISH("Spanish"),
//    FRENCH("French"),
//    GERMAN("German")

}

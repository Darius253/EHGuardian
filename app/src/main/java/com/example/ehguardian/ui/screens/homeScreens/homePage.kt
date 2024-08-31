package com.example.ehguardian.ui.screens.homeScreens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.example.ehguardian.ui.screens.homeScreens.dataScreen.DataHistoryScreen
import com.example.ehguardian.ui.screens.homeScreens.home.Home
import com.example.ehguardian.ui.screens.homeScreens.measureScreen.MeasureScreen
import com.example.ehguardian.ui.screens.homeScreens.profile.ProfileScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen() {
    // State to keep track of the selected item in the bottom navigation
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }

    // List of bottom navigation items
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Health,
        BottomNavItem.History,
        BottomNavItem.Profile
    )

    // Scaffold with a BottomNavigation and screen content
    Scaffold(
        bottomBar = {
            BottomNavigation(
                items = items,
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it }
            )
        }
    ) { innerPadding ->
        HomeScreenContents(
            selectedItem = selectedItem,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreenContents(
    selectedItem: Int,
    modifier: Modifier
) {
    // Display content based on the selected item
    when (selectedItem) {
        0 -> Home(modifier = modifier)
        1 -> MeasureScreen(modifier = modifier)
        2 -> DataHistoryScreen(modifier = modifier)
        3 -> ProfileScreen(modifier = modifier)
        else -> Home(modifier = modifier) // Default to Home if no match
    }
}

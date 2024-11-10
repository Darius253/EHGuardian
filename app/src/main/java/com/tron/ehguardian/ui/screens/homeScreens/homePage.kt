package com.tron.ehguardian.ui.screens.homeScreens



import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.tron.ehguardian.ui.screens.homeScreens.healthDataScreen.HealthDataScreen
import com.tron.ehguardian.ui.screens.homeScreens.home.Home
import com.tron.ehguardian.ui.screens.homeScreens.measureScreen.MeasureScreen
import com.tron.ehguardian.ui.screens.homeScreens.profile.ProfileScreen


@Composable
fun HomeScreen(
    onSignOutSuccess: () -> Unit,
    onDeleteAccountSuccess: () -> Unit

) {
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
            modifier = Modifier.padding(innerPadding),
            onSignOutSuccess = { onSignOutSuccess() },
            onDeleteAccountSuccess = {onDeleteAccountSuccess()}
        )
    }
}



@Composable
fun HomeScreenContents(
    selectedItem: Int,
    modifier: Modifier,
    onSignOutSuccess: () -> Unit = {},
    onDeleteAccountSuccess: () -> Unit = {}
) {
    // Display content based on the selected item
    when (selectedItem) {
        0 -> Home(modifier = modifier, onSignOutSuccess = { onSignOutSuccess() },
            onDeleteAccountSuccess = { onDeleteAccountSuccess ()})
        1 -> MeasureScreen()
        2 -> HealthDataScreen(modifier = modifier)
        3 -> ProfileScreen(modifier = modifier)
        else -> Home(modifier = modifier,onSignOutSuccess = { onSignOutSuccess() },
            onDeleteAccountSuccess = { onDeleteAccountSuccess ()} ) // Default to Home if no match
    }
}

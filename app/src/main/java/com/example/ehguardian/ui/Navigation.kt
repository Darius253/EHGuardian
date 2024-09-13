package com.example.ehguardian.ui



import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ehguardian.ui.screens.authenticationScreens.AuthenticationScreen
import com.example.ehguardian.ui.screens.homeScreens.HomeScreen

// Enum class to define the different navigation destinations
enum class NavigationClass(val route: String) {
    AuthenticationDestination(route = "authentication"),
    HomeDestination(route = "home"),
    SettingsDestination(route = "settings")
}


@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {

    NavHost(
        navController = navController,
        startDestination = NavigationClass.AuthenticationDestination.route,
        modifier = modifier
    ) {
        composable(route = NavigationClass.AuthenticationDestination.route) {
            AuthenticationScreen(
                modifier = modifier,
                onSignInClick = {
                    navController.navigate(NavigationClass.HomeDestination.route)
                },
                onSignUpClick = {
                        navController.navigate(NavigationClass.HomeDestination.route)
                },
                onForgotPasswordClick = {}
            )
        }
        composable(route = NavigationClass.HomeDestination.route) {
            // Home Screen
            HomeScreen(
                onSignOutSuccess = {

                    navController.navigate(NavigationClass.AuthenticationDestination.route)
                }
            )
        }

        }

    }


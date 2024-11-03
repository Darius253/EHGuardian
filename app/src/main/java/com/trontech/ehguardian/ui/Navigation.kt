package com.trontech.ehguardian.ui




import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.trontech.ehguardian.ui.screens.authenticationScreens.AuthenticationScreen
import com.trontech.ehguardian.ui.screens.authenticationScreens.ForgotPasswordScreen
import com.trontech.ehguardian.ui.screens.homeScreens.HomeScreen

// Enum class to define the different navigation destinations
enum class NavigationClass(val route: String) {
    AuthenticationDestination(route = "authentication"),
    HomeDestination(route = "home"),
    ForgotPasswordDestination(route = "forgotPassword")
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
                    navController.navigate(NavigationClass.HomeDestination.route) {
                        popUpTo(NavigationClass.AuthenticationDestination.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onSignUpClick = {
                    navController.navigate(NavigationClass.HomeDestination.route) {
                        popUpTo(NavigationClass.AuthenticationDestination.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onForgotPasswordClick = {
                    navController.navigate(NavigationClass.ForgotPasswordDestination.route) {

                    }
                }
            )
        }

        composable(route = NavigationClass.ForgotPasswordDestination.route) {
            ForgotPasswordScreen(

            )



        }

        composable(route = NavigationClass.HomeDestination.route) {
            // Home Screen
            HomeScreen(
                onSignOutSuccess = {
                    navController.navigate(NavigationClass.AuthenticationDestination.route){
                        popUpTo(NavigationClass.HomeDestination.route){
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onDeleteAccountSuccess={
                    navController.navigate(NavigationClass.AuthenticationDestination.route){
                        popUpTo(NavigationClass.HomeDestination.route){
                            inclusive = true
                        }
                        launchSingleTop = true






        }

    })
        }
    }
}



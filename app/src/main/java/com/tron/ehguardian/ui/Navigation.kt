package com.tron.ehguardian.ui




import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tron.ehguardian.ui.screens.authenticationScreens.AuthenticationScreen
import com.tron.ehguardian.ui.screens.authenticationScreens.ForgotPasswordScreen
import com.tron.ehguardian.ui.screens.homeScreens.HomeScreen
import com.tron.ehguardian.ui.screens.onBoardingScreen.OnBoardingScreen

// Enum class to define the different navigation destinations
enum class NavigationClass(val route: String) {
    OnboardingDestination (route = "onboarding"),
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
        startDestination = NavigationClass.OnboardingDestination.route,
        modifier = modifier
    ) {
        composable(route = NavigationClass.OnboardingDestination.route) {
            OnBoardingScreen(
                modifier = modifier,
                onFinalScreenClick = {
                    navController.navigate(NavigationClass.AuthenticationDestination.route) {
                        popUpTo(NavigationClass.OnboardingDestination.route) {
                            inclusive = true
                        }
                        launchSingleTop =true
                    }
                }
            )
        }
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
                onBackButtonClick = {
                    navController.navigate(NavigationClass.AuthenticationDestination.route) {
                        popUpTo(NavigationClass.ForgotPasswordDestination.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }

            )



        }

        composable(route = NavigationClass.HomeDestination.route) {
            val navigateToAuth = {
                navController.navigate(NavigationClass.AuthenticationDestination.route) {
                    popUpTo(NavigationClass.HomeDestination.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }

            HomeScreen(
                onSignOutSuccess = navigateToAuth,
                onDeleteAccountSuccess = navigateToAuth
            )
        }
    }
}



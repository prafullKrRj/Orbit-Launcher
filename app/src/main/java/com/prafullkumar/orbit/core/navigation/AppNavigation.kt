package com.prafullkumar.orbit.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.prafullkumar.hiddenapps.hiddenAppsModule
import com.prafullkumar.orbit.home.homeRoutes
import com.prafullkumar.orbit.onBoarding.OnBoardingScreen
import com.prafullkumar.orbit.settings.settingsGraph
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: Routes
) {
    NavHost(
        navController = navController,
        modifier = Modifier.fillMaxSize(),
        startDestination = startDestination // Start with onboarding
    ) {
        composable<Routes.OnboardingScreen> {
            OnBoardingScreen(
                viewModel = koinViewModel(),
                navController = navController
            )
        }
        homeRoutes(navController)
        settingsGraph(navController)
        usageGraph(navController)
        hiddenAppsNavGraph(navController)
    }
}
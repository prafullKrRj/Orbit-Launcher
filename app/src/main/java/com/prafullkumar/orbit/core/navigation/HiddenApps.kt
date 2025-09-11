package com.prafullkumar.orbit.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.prafullkumar.hiddenapps.HiddenAppRoutes
import com.prafullkumar.hiddenapps.presentation.hiddenAppsScreen.HiddenAppsScreen
import com.prafullkumar.hiddenapps.presentation.setPasswordScreen.SetPasswordScreen
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.hiddenAppsNavGraph(navController: NavHostController) {
    navigation<Routes.HiddenApps>(HiddenAppRoutes.HiddenAppsMain) {
        composable<HiddenAppRoutes.HiddenAppsMain> {
            HiddenAppsScreen(navController, koinViewModel())
        }
        composable<HiddenAppRoutes.SetPasswordScreen> {
            SetPasswordScreen(navController, koinViewModel())
        }
        composable<HiddenAppRoutes.UpdatePasswordScreen> {

        }
    }
}
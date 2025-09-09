package com.prafullkumar.orbit.usageScreen.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.prafullkumar.orbit.core.navigation.Routes
import com.prafullkumar.orbit.core.navigation.UsagesRoutes
import com.prafullkumar.orbit.usageScreen.presentation.screens.UsageScreen
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.usageGraph(
    navController: NavHostController
) {
    navigation<Routes.UsageScreen>(startDestination = UsagesRoutes.UsageMain) {
        composable<UsagesRoutes.UsageMain> {
            UsageScreen(navController, koinViewModel())
        }
    }
}
package com.prafullkumar.crazylauncher.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.prafullkumar.crazylauncher.appDrawer.AppDrawerScreen
import com.prafullkumar.crazylauncher.home.HomeScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = Routes.HomeScreen
    ) {
        composable<Routes.HomeScreen> {
            HomeScreen(koinViewModel(), navController)
        }
        composable<Routes.AppDrawerScreen> {
            AppDrawerScreen(koinViewModel(), navController)
        }
        settingsGraph(navController)
    }
}




fun NavGraphBuilder.settingsGraph(
    navController: NavController
) {
    navigation<Routes.SettingsScreen>(startDestination = SettingsRoutes.SettingsMain) {
        composable<SettingsRoutes.SettingsMain> {
            Box(Modifier.fillMaxSize()) {}
        }
    }
}
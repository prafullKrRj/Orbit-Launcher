package com.prafullkumar.crazylauncher.navigation

import kotlinx.serialization.Serializable

sealed interface Routes {


    @Serializable
    data object HomeScreen: Routes

    @Serializable
    data object AppDrawerScreen: Routes

    @Serializable
    data object SettingsScreen: Routes

    @Serializable
    data object HabitsScreen: Routes

    @Serializable
    data object WellBeingScreen: Routes

    @Serializable
    data object DrawerSettings: Routes
}
sealed interface SettingsRoutes {

    @Serializable
    data object SettingsMain: SettingsRoutes

}
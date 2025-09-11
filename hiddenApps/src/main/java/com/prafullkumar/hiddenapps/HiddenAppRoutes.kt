package com.prafullkumar.hiddenapps

import kotlinx.serialization.Serializable


sealed interface HiddenAppRoutes {

    @Serializable
    data object HiddenAppsMain : HiddenAppRoutes

    @Serializable
    data object SetPasswordScreen : HiddenAppRoutes

    @Serializable
    data object UpdatePasswordScreen : HiddenAppRoutes
}
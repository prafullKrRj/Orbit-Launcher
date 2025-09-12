package com.prafullkumar.orbit.home.utility

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.navigation.NavHostController

@Composable
fun UtilityScreen(
    navController: NavHostController,
    viewModel: UtilityViewModel
) {
    Column(Modifier.fillMaxSize()) {
        Text("Utility Screen", fontSize = TextUnit.Unspecified)
    }
}
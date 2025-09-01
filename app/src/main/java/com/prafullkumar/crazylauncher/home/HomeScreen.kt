package com.prafullkumar.crazylauncher.home

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.prafullkumar.crazylauncher.home.components.WatchComposable
import com.prafullkumar.crazylauncher.navigation.Routes

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    Scaffold(Modifier.fillMaxSize()) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            navController.navigate(Routes.AppDrawerScreen)
                        }
                    ) { _, dragAmount ->
                        if (dragAmount.y < -50) { // Reduced threshold for better sensitivity
//                            navController.navigate(Routes.AppDrawerScreen)
                        }
                    }
                }
                .verticalScroll(rememberScrollState())
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WatchComposable()
            Text(
                text = buildAnnotatedString {
                    val totalMinutes = viewModel.currentUsage / 60000
                    val hours = totalMinutes / 60
                    val minutes = totalMinutes % 60
                    append("Today's Usage: ")
                    if (hours > 0) {
                        append("${hours}h ${minutes}m")
                    } else {
                        append("${minutes}m")
                    }
                },
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Button(onClick = {
                navController.navigate(Routes.AppDrawerScreen)
            }) {
                Text("Open App Drawer")
            }
        }
    }
}
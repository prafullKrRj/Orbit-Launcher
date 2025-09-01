package com.prafullkumar.crazylauncher.appDrawer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@Composable
fun AppDrawerScreen(
    viewModel: AppDrawerViewModel,
    navController: NavController
) {
    val groupedApps by viewModel.groupedApps.collectAsStateWithLifecycle()
    val context = LocalContext.current
    Scaffold(
        topBar = {
            OutlinedTextField(
                value = "",
                onValueChange = { },
                placeholder = {
                    Text(
                        text = "Search apps...",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                ),
                singleLine = true
            )
        }
    ) { paddingValues ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(paddingValues)
        ) {
            groupedApps.forEach { (letter, apps) ->
                item {
                    Text(
                        text = letter.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(
                            top = 16.dp,
                            bottom = 8.dp,
                            start = 4.dp
                        )
                    )
                }

                items(apps, key = {
                    it.packageName
                }) { app ->
                    Text(
                        text = app.label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.launchApp(context, app)
                            }
                            .padding(
                                horizontal = 12.dp,
                                vertical = 10.dp
                            )
                    )
                }
            }
        }
    }
}
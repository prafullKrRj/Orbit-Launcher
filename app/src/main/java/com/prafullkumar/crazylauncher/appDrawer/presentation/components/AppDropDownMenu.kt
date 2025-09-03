package com.prafullkumar.crazylauncher.appDrawer.presentation.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AppDropDownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onUninstall: () -> Unit,
    onAddToFavorites: () -> Unit,
    onHideApp: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            text = { Text("Uninstall") },
            onClick = {
                onUninstall()
                onDismiss()
            }
        )
        DropdownMenuItem(
            text = { Text("Add to Favorites") },
            onClick = {
                onAddToFavorites()
                onDismiss()
            }
        )
        DropdownMenuItem(
            text = { Text("Hide App") },
            onClick = {
                onHideApp()
                onDismiss()
            }
        )
    }
}
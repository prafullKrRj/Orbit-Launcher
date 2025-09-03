package com.prafullkumar.crazylauncher.appDrawer.presentation.components

import android.content.Context
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.prafullkumar.crazylauncher.appDrawer.presentation.AppDrawerViewModel
import com.prafullkumar.crazylauncher.core.model.AppInfo
import com.prafullkumar.crazylauncher.core.utils.launchApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppGrid(
    paddingValues: PaddingValues,
    groupedApps: Map<Char, List<AppInfo>>,
    viewModel: AppDrawerViewModel,
    context: Context,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?,
    scope: CoroutineScope
) {
    val allApps = groupedApps.values.flatten()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 80.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    scope.launch {
                        change.consume()
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                }
            },
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(allApps, key = { it.packageName }) { app ->
            AppGridItem(
                app = app,
                onClick = { launchApp(context, app) },
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(400),
                    placementSpec = tween(400)
                ),
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun AppGridItem(
    app: AppInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AppDrawerViewModel
) {
    var showDropDown by remember { mutableStateOf(false) }
    Box {
        Column(
            modifier = modifier
                .aspectRatio(1f)
                .clickable(onClick = onClick)
                .background(
                    MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.3f),
                    RoundedCornerShape(20.dp)
                )
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        CircleShape
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                app.icon?.let { drawable ->
                    Image(
                        bitmap = drawable.toBitmap().asImageBitmap(),
                        contentDescription = "${app.label} icon",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
            }
        }
        AppDropDownMenu(
            expanded = showDropDown,
            onDismiss = { showDropDown = false },
            onUninstall = { /* TODO: viewModel.uninstallApp(app) */ },
            onAddToFavorites = { viewModel.addToFavorites(app) },
            onHideApp = { /* TODO: viewModel.hideApp(app) */ }
        )
    }
}
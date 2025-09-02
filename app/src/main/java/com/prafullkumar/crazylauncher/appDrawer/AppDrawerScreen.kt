package com.prafullkumar.crazylauncher.appDrawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.prafullkumar.crazylauncher.appDrawer.components.AppGrid
import com.prafullkumar.crazylauncher.appDrawer.components.AppGridWithLabels
import com.prafullkumar.crazylauncher.appDrawer.components.AppList
import com.prafullkumar.crazylauncher.appDrawer.components.AppListWithIcons
import com.prafullkumar.crazylauncher.appDrawer.drawerSettings.LayoutType
import com.prafullkumar.crazylauncher.navigation.Routes

@Composable
fun AppDrawerScreen(
    viewModel: AppDrawerViewModel,
    navController: NavController,
    onDismiss: (() -> Unit)? = null
) {
    val groupedApps by viewModel.groupedApps.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val layoutType by viewModel.layoutType.collectAsStateWithLifecycle(initialValue = LayoutType.LIST)

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    
    var dragOffset by remember { mutableFloatStateOf(0f) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (dragOffset > 200f) { // Dismiss threshold
                            onDismiss?.invoke()
                        }
                        dragOffset = 0f
                    }
                ) { _, dragAmount ->
                    if (dragAmount > 0) { // Only track downward drags
                        dragOffset += dragAmount
                    }
                }
            }
            .systemBarsPadding(),
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        topBar = {
            DrawerTopBar(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged,
                onSettingsClick = {
                    navController.navigate(Routes.DrawerSettings)
                }
            )
        }
    ) { paddingValues ->
        when(layoutType) {
            LayoutType.LIST -> {
                AppList(
                    paddingValues = paddingValues,
                    groupedApps = groupedApps,
                    viewModel = viewModel,
                    context = context,
                    focusManager = focusManager,
                    keyboardController = keyboardController,
                    scope = scope
                )
            }
            LayoutType.LIST_WITH_ICONS -> {
                AppListWithIcons(
                    paddingValues = paddingValues,
                    groupedApps = groupedApps,
                    viewModel = viewModel,
                    context = context,
                    focusManager = focusManager,
                    keyboardController = keyboardController,
                    scope = scope
                )
            }
            LayoutType.GRID -> {
                AppGrid(
                    paddingValues = paddingValues,
                    groupedApps = groupedApps,
                    viewModel = viewModel,
                    context = context,
                    focusManager = focusManager,
                    keyboardController = keyboardController,
                    scope = scope
                )
            }
            LayoutType.GRID_WITH_LABELS -> {
                AppGridWithLabels(
                    paddingValues = paddingValues,
                    groupedApps = groupedApps,
                    viewModel = viewModel,
                    context = context,
                    focusManager = focusManager,
                    keyboardController = keyboardController,
                    scope = scope
                )
            }
        }
    }
}

@Composable
private fun DrawerTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSettingsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
    ) {
        // Drag handle
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                .align(Alignment.TopCenter)
        )
        
        // Search bar
        SearchBar(
            query = query,
            onQueryChange = onQueryChange,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            onSettingsClick = onSettingsClick
        )
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    focusManager: FocusManager = LocalFocusManager.current,
    onSettingsClick: () -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .clip(CircleShape),
            placeholder = {
                Text(
                    text = "Search apps...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            },
            shape = CircleShape,
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(
                    alpha = 0.9f
                ),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer.copy(
                    alpha = 0.8f
                ),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary
                    )
        )

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable(onClick = onSettingsClick)
                .background(
                    MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f),
                    CircleShape
                ), contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
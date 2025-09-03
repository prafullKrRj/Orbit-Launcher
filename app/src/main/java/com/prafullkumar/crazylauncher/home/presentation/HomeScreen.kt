package com.prafullkumar.crazylauncher.home.presentation

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.prafullkumar.crazylauncher.appDrawer.presentation.AppDrawerScreen
import com.prafullkumar.crazylauncher.core.utils.launchApp
import com.prafullkumar.crazylauncher.home.presentation.components.WatchComposable
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    var showAppDrawer by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    val favApps by viewModel.favApps.collectAsState()
    val scope = rememberCoroutineScope()
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        bottomBar = {
            BottomAppBar(
                onPhoneClick = {
                    scope.launch {
                        openIntent(context, Intent.ACTION_DIAL)
                    }
                },
                onMessagesClick = {
                    scope.launch {
                        val intent = Intent(Intent.ACTION_MAIN).apply {
                            addCategory(Intent.CATEGORY_APP_MESSAGING)
                        }
                        openIntent(context, intent = intent, action = "")
                    }
                },
                onDrawerClick = { showAppDrawer = true },
                onCameraClick = {
                    scope.launch {
                        val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                        openIntent(context, intent = intent, action = "")
                    }
                }
            )
        }) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            WatchComposable(viewModel)

            UsageComposable(viewModel)

            if (favApps.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    favApps.forEach { app ->
                        Text(
                            text = app.label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = {
                                        launchApp(context, app)
                                    }
                                )
                                .padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Bottom Sheet App Drawer
        if (showAppDrawer) {
            ModalBottomSheet(
                onDismissRequest = { showAppDrawer = false },
                sheetState = bottomSheetState,
                dragHandle = null,
                contentWindowInsets = {
                    WindowInsets(0, 0, 0, 0)
                },
            ) {
                AppDrawerScreen(
                    viewModel = koinViewModel(),
                    navController = navController,
                    onDismiss = { showAppDrawer = false }
                )
            }
        }
    }
}

fun openIntent(context: Context, action: String, intent: Intent? = null) {
    try {
        intent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } ?: run {
            val intent = Intent(action)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    } catch (e: Exception) {
        println(e.message)
    }
}

@Composable
fun BottomAppBar(
    onPhoneClick: () -> Unit, onMessagesClick: () -> Unit, onDrawerClick: () -> Unit,
    onCameraClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side - Phone and Messages
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onPhoneClick)
                    .background(
                        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f), CircleShape
                    ), contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Phone",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onMessagesClick)
                    .background(
                        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f), CircleShape
                    ), contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Message,
                    contentDescription = "Messages",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onCameraClick)
                    .background(
                        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f), CircleShape
                    ), contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Camera",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Right side - Drawer
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable(onClick = onDrawerClick)
                .background(
                    MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f), CircleShape
                ), contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.List,
                contentDescription = "App Drawer",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
@Composable
fun UsageComposable(viewModel: HomeViewModel) {
    val context = LocalContext.current
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
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .pointerInput(Unit) {
                detectTapGestures {
                    openDigitalWellBeing(context)
                }
            }
    )
}

fun openDigitalWellBeing(context: Context) {
    try {
        val intent = Intent("android.settings.DIGITAL_WELLBEING_SETTINGS")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to general usage settings if Digital Wellbeing is not available
        println(e.message)
        try {
            val fallbackIntent = Intent("android.settings.USAGE_ACCESS_SETTINGS")
            fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(fallbackIntent)
        } catch (ex: Exception) {
            // If both fail, open general settings
            val settingsIntent = Intent(android.provider.Settings.ACTION_SETTINGS)
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(settingsIntent)
        }
    }
}
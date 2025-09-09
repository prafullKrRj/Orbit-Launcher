package com.prafullkumar.orbit.core.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import com.prafullkumar.orbit.core.data.usageData.getInstalledAppsMain
import com.prafullkumar.orbit.core.model.AppInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class InstalledAppsCaches(
    private val context: Context
) {
    private val _installedApps = MutableStateFlow<Set<AppInfo>>(emptySet())

    init {
        populateInstalledApps()
    }

    private fun populateInstalledApps() {
        CoroutineScope(Dispatchers.IO).launch {
            val apps = getInstalledAppsMain(context)
            _installedApps.update {
                apps.toSet()
            }
        }
    }

    fun getInstalledApps(): Flow<Set<AppInfo>> = _installedApps

    fun removeApp(appInfo: AppInfo) {
        _installedApps.update {
            it.minus(appInfo)
        }
    }

    fun addApps(apps: List<AppInfo>) {
        _installedApps.update {
            it.plus(apps)
        }
    }

    fun removeFromPackageName(packageName: String) {
        _installedApps.update {
            it.filterNot { app -> app.packageName == packageName }.toSet()
        }
    }

    fun addApp(appInfo: AppInfo) {
        _installedApps.update {
            it.plus(appInfo)
        }
    }
}

class AppInstallReceiver : BroadcastReceiver(), KoinComponent {
    private val installedAppsCaches by inject<InstalledAppsCaches>()

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REPLACED -> {
                val isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                val packageName = intent.data?.encodedSchemeSpecificPart
                if (packageName != null && !isReplacing) {
                    Log.d("AppInstallReceiver", "New app installed: $packageName")
                    handleAppInstalled(context, packageName)
                }
            }
        }
    }

    private fun handleAppInstalled(context: Context?, packageName: String) {
        val packageManager = context?.packageManager ?: return
        try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            val appName = packageManager.getApplicationLabel(applicationInfo).toString()
            Log.d("AppInstallReceiver", "App name: $appName")
            installedAppsCaches.addApp(
                AppInfo(
                    packageName = packageName,
                    label = appName,
                    icon = packageManager.getApplicationIcon(packageName),
                    launchIntent = packageManager.getLaunchIntentForPackage(packageName)
                )
            )
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("AppInstallReceiver", "Error getting app info", e)
        }
    }

    companion object {
        fun register(context: Context): AppInstallReceiver {
            val receiver = AppInstallReceiver()
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_PACKAGE_ADDED)
                addAction(Intent.ACTION_PACKAGE_REPLACED)
                addDataScheme("package")
            }
            context.registerReceiver(receiver, filter)
            return receiver
        }

        fun unregister(context: Context, receiver: AppInstallReceiver) {
            context.unregisterReceiver(receiver)
        }
    }
}

class AppUninstallReceiver : BroadcastReceiver(), KoinComponent {
    private val installedAppsCaches by inject<InstalledAppsCaches>()

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_PACKAGE_REMOVED, Intent.ACTION_PACKAGE_FULLY_REMOVED -> {
                // Check if this is a full removal and not just a replacement
                val isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                if (!isReplacing) {
                    val packageName = intent.data?.encodedSchemeSpecificPart
                    if (packageName != null) {
                        // An app has been uninstalled
                        Log.d("AppUninstallReceiver", "App uninstalled: $packageName")

                        // Remove the app from cache
                        installedAppsCaches.removeFromPackageName(packageName)
                    }
                }
            }
        }
    }

    companion object {
        fun register(context: Context): AppUninstallReceiver {
            val receiver = AppUninstallReceiver()
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_PACKAGE_REMOVED)
                addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
                addDataScheme("package")
            }
            context.registerReceiver(receiver, filter)
            return receiver
        }

        fun unregister(context: Context, receiver: AppUninstallReceiver) {
            context.unregisterReceiver(receiver)
        }
    }
}
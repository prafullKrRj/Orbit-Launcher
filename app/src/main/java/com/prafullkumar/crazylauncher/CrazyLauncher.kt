package com.prafullkumar.crazylauncher

import android.app.Application
import com.prafullkumar.crazylauncher.appDrawer.AppDrawerViewModel
import com.prafullkumar.crazylauncher.appDrawer.drawerSettings.DrawerSettingsPreferenceStore
import com.prafullkumar.crazylauncher.appDrawer.drawerSettings.DrawerSettingsViewModel
import com.prafullkumar.crazylauncher.home.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class CrazyLauncher : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CrazyLauncher)
            modules(
                module {
                    viewModel {
                        HomeViewModel(get())
                    }
                    single {
                        DrawerSettingsPreferenceStore(get())
                    }
                    viewModel {
                        AppDrawerViewModel(get())
                    }
                    viewModel { DrawerSettingsViewModel() }
                }
            )
        }
    }
}
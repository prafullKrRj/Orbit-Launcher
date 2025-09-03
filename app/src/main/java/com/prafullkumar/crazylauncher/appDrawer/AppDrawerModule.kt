package com.prafullkumar.crazylauncher.appDrawer

import com.prafullkumar.crazylauncher.appDrawer.data.repository.AppDrawerRepository
import com.prafullkumar.crazylauncher.appDrawer.presentation.AppDrawerViewModel
import com.prafullkumar.crazylauncher.appDrawer.data.DrawerSettingsPreferenceStore
import com.prafullkumar.crazylauncher.appDrawer.presentation.drawerSettings.DrawerSettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val appDrawerModule = module {
    single<AppDrawerRepository> {
        AppDrawerRepository(context = get())
    }
    single {
        DrawerSettingsPreferenceStore(get())
    }
    viewModel {
        AppDrawerViewModel(get())
    }
    viewModel { DrawerSettingsViewModel() }

}
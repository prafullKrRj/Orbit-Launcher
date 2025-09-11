package com.prafullkumar.orbit.home

import androidx.room.Room
import com.prafullkumar.orbit.home.data.DrawerSettingsPreferenceStore
import com.prafullkumar.orbit.home.data.local.HiddenAppsDatabase
import com.prafullkumar.orbit.home.data.repository.AppDrawerRepository
import com.prafullkumar.orbit.home.data.repository.HiddenAppsRepository
import com.prafullkumar.orbit.home.data.repository.HomeRepository
import com.prafullkumar.orbit.home.presentation.screens.appDrawer.AppDrawerViewModel
import com.prafullkumar.orbit.home.presentation.screens.drawerSettings.DrawerSettingsViewModel
import com.prafullkumar.orbit.home.presentation.screens.home.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val homeModule = module {
    single {
        HomeRepository(get(), get(), get())
    }
    viewModel {
        HomeViewModel(get())
    }
    single<AppDrawerRepository> {
        AppDrawerRepository(context = get())
    }
    single {
        DrawerSettingsPreferenceStore(get())
    }
    viewModel {
        AppDrawerViewModel()
    }
    viewModel { DrawerSettingsViewModel() }
    single {
        Room.databaseBuilder(
            get(),
            HiddenAppsDatabase::class.java,
            "hidden_apps_db"
        ).fallbackToDestructiveMigration().build()
    }
    single {
        get<HiddenAppsDatabase>().hiddenAppsDao()
    }
    single {
        HiddenAppsRepository(get(), get())
    }

}
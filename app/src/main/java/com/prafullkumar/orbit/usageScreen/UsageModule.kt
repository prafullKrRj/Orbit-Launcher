package com.prafullkumar.orbit.usageScreen

import android.app.Application
import com.prafullkumar.orbit.usageScreen.data.UsageRepository
import com.prafullkumar.orbit.usageScreen.presentation.screens.UsageScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val usageModule = module {
    single { UsageRepository(get<Application>()) }
    viewModel { UsageScreenViewModel(get()) }
}
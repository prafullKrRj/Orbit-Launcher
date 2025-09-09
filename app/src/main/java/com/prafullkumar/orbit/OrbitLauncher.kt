package com.prafullkumar.orbit

import android.app.Application
import androidx.room.Room
import com.prafullkumar.orbit.core.data.InstalledAppsCaches
import com.prafullkumar.orbit.core.data.local.FavDao
import com.prafullkumar.orbit.core.data.local.FavDatabase
import com.prafullkumar.orbit.home.homeModule
import com.prafullkumar.orbit.onBoarding.OnBoardingViewModel
import com.prafullkumar.orbit.usageScreen.usageModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class OrbitLauncher : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@OrbitLauncher)
            modules(
                homeModule,
                usageModule,
                module {

                    single<FavDatabase> {
                        Room.databaseBuilder(
                            get(),
                            FavDatabase::class.java,
                            "fav_database"
                        ).fallbackToDestructiveMigration().build()
                    }
                    single<FavDao> {
                        get<FavDatabase>().favDao()
                    }
                    single {
                        InstalledAppsCaches(get<Application>())
                    }
                    viewModel {
                        OnBoardingViewModel(get())
                    }
                }
            )
        }
    }
}
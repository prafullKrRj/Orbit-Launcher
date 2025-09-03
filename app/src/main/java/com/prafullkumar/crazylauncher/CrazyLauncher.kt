package com.prafullkumar.crazylauncher

import android.app.Application
import androidx.room.Room
import com.prafullkumar.crazylauncher.appDrawer.appDrawerModule
import com.prafullkumar.crazylauncher.core.data.local.FavDao
import com.prafullkumar.crazylauncher.core.data.local.FavDatabase
import com.prafullkumar.crazylauncher.home.data.HomeRepository
import com.prafullkumar.crazylauncher.home.presentation.HomeViewModel
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
                appDrawerModule,
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
                       HomeRepository(get(), get())
                    }
                    viewModel {
                        HomeViewModel(get(), get())
                    }


                }
            )
        }
    }
}
package com.lazyhat.novsuapp.ui

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import com.lazyhat.novsuapp.di.mainModule

class MainApplication : Application() /*, HasTracerConfiguration*/ {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(mainModule)
        }
    }

//    override val tracerConfiguration: List<TracerConfiguration>
//        get() = listOf(
//            CoreTracerConfiguration.build {
//                // опции ядра трейсера
//            },
//            CrashReportConfiguration.build {
//                // опции сборщика крэшей
//            },
//            CrashFreeConfiguration.build {
//                // опции подсчета crash free
//            }
//        )
}
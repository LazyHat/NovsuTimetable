package com.lazyhat.novsuapp.di

import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import com.lazyhat.novsuapp.data.local.GroupParametersLocalSource
import com.lazyhat.novsuapp.data.local.GroupParametersLocalSourceImpl
import com.lazyhat.novsuapp.data.local.GroupParametersSerializer
import com.lazyhat.novsuapp.data.net.NetworkSource
import com.lazyhat.novsuapp.data.net.NetworkSourceImpl
import com.lazyhat.novsuapp.data.repo.MainRepository
import com.lazyhat.novsuapp.data.repo.MainRepositoryImpl
import com.lazyhat.novsuapp.ui.screens.groupsettings.GroupSettingsScreenViewModel
import com.lazyhat.novsuapp.ui.screens.timetable.TimetableScreenViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {
    single { Dispatchers.IO }
    single {
        DataStoreFactory.create(
            GroupParametersSerializer,
            produceFile = { androidContext().dataStoreFile("group") },
            corruptionHandler = ReplaceFileCorruptionHandler { null },
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }
    single {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json()
            }
        }
    }
    single<GroupParametersLocalSource> {
        GroupParametersLocalSourceImpl(
            get(),
            get()
        )
    }
    single<NetworkSource> { NetworkSourceImpl(get(), get()) }
    single<MainRepository> { MainRepositoryImpl(get(), get()) }
    viewModel { TimetableScreenViewModel(get()) }
    viewModel { GroupSettingsScreenViewModel(get()) }
}
package ui

import helper.LocationSyncRequestStore
import helper.SharedPreferencesLocationSyncRequestStore
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ui.other.DebouncerManager

val coreDi = module {
    factory { (coroutineScope: CoroutineScope, delay: Long) ->
        DebouncerManager(coroutineScope, delay)
    }

    single<LocationSyncRequestStore> {
        SharedPreferencesLocationSyncRequestStore(androidContext())
    }
}

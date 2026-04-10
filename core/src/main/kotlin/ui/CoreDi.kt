package ui

import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module
import ui.other.DebouncerManager

val coreDi = module {
    factory { (coroutineScope: CoroutineScope, delay: Long) ->
        DebouncerManager(coroutineScope, delay)
    }
}
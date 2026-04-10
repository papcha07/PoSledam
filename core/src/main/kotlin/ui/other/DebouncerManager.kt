package ui.other

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DebouncerManager(
    private val coroutineScope: CoroutineScope,
    private val delayTime: Long
) {
    private var job: Job? = null

    fun debounce(action: () -> Unit) {
        job?.cancel()
        job = coroutineScope.launch {
            delay(delayTime)
            action()
        }
    }

    fun cancel() {
        job?.cancel()
    }
}
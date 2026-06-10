package worker.location_worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import helper.hasBackgroundLocationPermission
import helper.hasForegroundLocationPermission
import java.util.concurrent.TimeUnit

class WorkerInteractorImpl(
    private val context: Context
) : WorkerInteractor {
    private val workManager = WorkManager.getInstance(context)
    override fun startLocationWorker() {
        if (!hasRequiredLocationPermission()) return
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<LocationWorker>(
            repeatInterval = 15,
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            LOCATION_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    override fun stopLocationWorker() {
        workManager.cancelUniqueWork(LOCATION_WORK_NAME)
    }

    private fun hasRequiredLocationPermission(): Boolean {
        return context.hasForegroundLocationPermission() &&
                context.hasBackgroundLocationPermission()
    }

    companion object {
        private const val LOCATION_WORK_NAME = "location_work"
    }
}

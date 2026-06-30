package worker.location_worker

/*
Background location worker is temporarily disabled for moderation.

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import domain.interactor.location.LocationInteractor
import domain.interactor.location.LocationSendResult
import helper.hasBackgroundLocationPermission
import helper.hasForegroundLocationPermission

class LocationWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val locationInteractor: LocationInteractor
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        Log.d("WORKER_MANAGER", "Location worker started")

        if (!applicationContext.hasForegroundLocationPermission()) {
            Log.d("WORKER_MANAGER", "Location worker skipped: foreground permission is missing")
            return Result.success()
        }

        if (!applicationContext.hasBackgroundLocationPermission()) {
            Log.d("WORKER_MANAGER", "Location worker skipped: background permission is missing")
            return Result.success()
        }

        return try {
            when (val result = locationInteractor.sendCurrentLocation()) {
                LocationSendResult.Success -> {
                    Log.d("WORKER_MANAGER", "Location worker finished successfully")
                    Result.success()
                }

                LocationSendResult.PermissionDenied -> {
                    Log.d("WORKER_MANAGER", "Location worker skipped: permission denied")
                    Result.success()
                }

                LocationSendResult.LocationUnavailable -> {
                    Log.d("WORKER_MANAGER", "Location worker skipped: location unavailable")
                    Result.success()
                }

                is LocationSendResult.NetworkError -> {
                    Log.d("WORKER_MANAGER", "Location worker failed while sending location")
                    if (result.retryable) Result.retry() else Result.failure()
                }
            }
        } catch (e: SecurityException) {
            Log.e("WORKER_MANAGER", "Location permission denied", e)
            Result.success()
        } catch (e: Exception) {
            Log.e("WORKER_MANAGER", "Temporary error", e)
            Result.retry()
        }
    }
}
*/

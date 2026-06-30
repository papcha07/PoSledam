package worker.location_worker.factory

/*
Background location worker is temporarily disabled for moderation.

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import domain.interactor.location.LocationInteractor
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import worker.location_worker.LocationWorker


class KoinWorkerFactory : WorkerFactory(), KoinComponent {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        Log.d("KoinWorkerFactory", "Trying to create worker: $workerClassName")
        return when (workerClassName) {

            LocationWorker::class.java.name -> LocationWorker(
                appContext,
                workerParameters,
                locationInteractor = get<LocationInteractor>()
            )

            else -> null
        }
    }
}
*/

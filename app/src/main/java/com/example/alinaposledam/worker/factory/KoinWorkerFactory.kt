package com.example.alinaposledam.worker.factory

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.alinaposledam.worker.LocationWorker
import domain.interactor.LocationInteractor
import org.koin.core.component.KoinComponent
import org.koin.core.component.get


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
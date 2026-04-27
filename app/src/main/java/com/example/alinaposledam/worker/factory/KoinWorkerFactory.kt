package com.example.alinaposledam.worker.factory

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.alinaposledam.worker.LocationWorker
import org.koin.core.component.KoinComponent
import org.koin.core.component.get


class KoinWorkerFactory(
    private val context: Context
) : WorkerFactory(), KoinComponent {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {

            LocationWorker::class.java.name -> LocationWorker(
                appContext,
                workerParameters,
                get()
            )

            else -> null
        }
    }
}
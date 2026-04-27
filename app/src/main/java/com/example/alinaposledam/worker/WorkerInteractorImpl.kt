package com.example.alinaposledam.worker

import android.content.Context
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class WorkerInteractorImpl(
    private val context: Context
) : WorkerInteractor {

    private val workManager = WorkManager.getInstance(context)
    override suspend fun sendLocation() {
        val locationBuilder = PeriodicWorkRequestBuilder<LocationWorker>(
            repeatInterval = 15,
            repeatIntervalTimeUnit = TimeUnit.MINUTES,
            flexTimeInterval = 15,
            flexTimeIntervalUnit = TimeUnit.MINUTES
        )
        workManager.enqueue(locationBuilder.build())
    }
}
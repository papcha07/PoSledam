package com.example.alinaposledam.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class WorkerInteractorImpl(
    private val context: Context
) : WorkerInteractor {

    private val workManager = WorkManager.getInstance(context)
    override fun sendLocation() {

        val request = PeriodicWorkRequestBuilder<LocationWorker>(
            repeatInterval = 15,
            repeatIntervalTimeUnit = TimeUnit.MINUTES,
            flexTimeInterval = 15,
            flexTimeIntervalUnit = TimeUnit.MINUTES
        ).build()

        workManager.enqueueUniquePeriodicWork(
            LOCATION_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    companion object {
        private const val LOCATION_WORK_NAME = "location_work"
    }
}
package com.example.alinaposledam.worker.location_worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import domain.interactor.LocationInteractor

class LocationWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val locationInteractor: LocationInteractor
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        Log.d("WORKER_MANAGER", "START")
        return try {
            locationInteractor.sendCurrentLocation()
            Log.d("WORKER_MANAGER", "GOOD")
            Result.success()
        } catch (e: Exception) {
            Log.d("WORKER_MANAGER", "FAILED")
            Result.failure()
        }
    }
}
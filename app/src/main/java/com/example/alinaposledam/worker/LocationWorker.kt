package com.example.alinaposledam.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import domain.interactor.LocationInteractor

class LocationWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val locationInteractor: LocationInteractor
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        return try {
            locationInteractor.sendCurrentLocation()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

}
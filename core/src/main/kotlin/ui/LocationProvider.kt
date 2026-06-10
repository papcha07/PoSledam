package ui

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import helper.hasForegroundLocationPermission
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

class LocationProvider(
    context: Context
) {

    private val context = context.applicationContext
    private val client = LocationServices.getFusedLocationProviderClient(context)

    suspend fun getCurrentLocation(): Location? {
        if (!context.hasForegroundLocationPermission()) {
            return null
        }

        return requestCurrentLocation()
            ?: getLastLocation()
            ?: requestSingleLocationUpdate()
    }

    @SuppressLint("MissingPermission")
    private suspend fun requestCurrentLocation(): Location? = suspendCancellableCoroutine { cont ->
        val cancellationTokenSource = CancellationTokenSource()
        cont.invokeOnCancellation { cancellationTokenSource.cancel() }
        client.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location ->
            cont.resumeIfActive(location)
        }.addOnFailureListener {
            cont.resumeIfActive(null)
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getLastLocation(): Location? = suspendCancellableCoroutine { cont ->
        client.lastLocation
            .addOnSuccessListener { location -> cont.resumeIfActive(location) }
            .addOnFailureListener { cont.resumeIfActive(null) }
    }

    @SuppressLint("MissingPermission")
    private suspend fun requestSingleLocationUpdate(): Location? =
        withTimeoutOrNull(LOCATION_UPDATE_TIMEOUT_MS) {
            suspendCancellableCoroutine { cont ->
                val request = LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    LOCATION_UPDATE_INTERVAL_MS
                )
                    .setMaxUpdates(1)
                    .setDurationMillis(LOCATION_UPDATE_TIMEOUT_MS)
                    .build()

                val callback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        client.removeLocationUpdates(this)
                        cont.resumeIfActive(result.lastLocation)
                    }
                }

                cont.invokeOnCancellation {
                    client.removeLocationUpdates(callback)
                }

                client.requestLocationUpdates(
                    request,
                    callback,
                    Looper.getMainLooper()
                ).addOnFailureListener {
                    client.removeLocationUpdates(callback)
                    cont.resumeIfActive(null)
                }
            }
        }

    private fun kotlinx.coroutines.CancellableContinuation<Location?>.resumeIfActive(location: Location?) {
        if (isActive) {
            resume(location)
        }
    }

    private companion object {
        const val LOCATION_UPDATE_INTERVAL_MS = 1_000L
        const val LOCATION_UPDATE_TIMEOUT_MS = 10_000L
    }
}

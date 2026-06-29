package ui

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
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
            Log.d("USER_LOCATION", "LocationProvider: foreground permission is missing")
            return null
        }

        getLastLocation()?.takeIf { it.isFreshEnough() }?.let { location ->
            Log.d("USER_LOCATION", "LocationProvider: fresh last location returned")
            return location
        }

        requestCurrentLocation()?.let { location ->
            Log.d("USER_LOCATION", "LocationProvider: current location returned")
            return location
        }

        requestSingleLocationUpdate()?.let { location ->
            Log.d("USER_LOCATION", "LocationProvider: single location update returned")
            return location
        }

        getLastLocation()?.let { location ->
            Log.d("USER_LOCATION", "LocationProvider: stale last location returned")
            return location
        }

        Log.d("USER_LOCATION", "LocationProvider: no location available")
        return null
    }

    @SuppressLint("MissingPermission")
    private suspend fun requestCurrentLocation(): Location? =
        withTimeoutOrNull(CURRENT_LOCATION_TIMEOUT_MS) {
            suspendCancellableCoroutine { cont ->
                val cancellationTokenSource = CancellationTokenSource()
                cont.invokeOnCancellation { cancellationTokenSource.cancel() }
                client.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { location ->
                    cont.resumeIfActive(location)
                }.addOnFailureListener { error ->
                    Log.e("USER_LOCATION", "LocationProvider: getCurrentLocation failed", error)
                    cont.resumeIfActive(null)
                }
            }
        }.also { location ->
            if (location == null) {
                Log.d("USER_LOCATION", "LocationProvider: current location timeout or null")
            }
        }

    @SuppressLint("MissingPermission")
    private suspend fun getLastLocation(): Location? = suspendCancellableCoroutine { cont ->
        client.lastLocation
            .addOnSuccessListener { location -> cont.resumeIfActive(location) }
            .addOnFailureListener { error ->
                Log.e("USER_LOCATION", "LocationProvider: lastLocation failed", error)
                cont.resumeIfActive(null)
            }
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
                ).addOnFailureListener { error ->
                    Log.e("USER_LOCATION", "LocationProvider: requestLocationUpdates failed", error)
                    client.removeLocationUpdates(callback)
                    cont.resumeIfActive(null)
                }
            }
        }.also { location ->
            if (location == null) {
                Log.d("USER_LOCATION", "LocationProvider: single update timeout or null")
            }
        }

    private fun kotlinx.coroutines.CancellableContinuation<Location?>.resumeIfActive(location: Location?) {
        if (isActive) {
            resume(location)
        }
    }

    private fun Location.isFreshEnough(): Boolean {
        return System.currentTimeMillis() - time <= LOCATION_MAX_AGE_MS
    }

    private companion object {
        const val CURRENT_LOCATION_TIMEOUT_MS = 5_000L
        const val LOCATION_UPDATE_INTERVAL_MS = 5_000L
        const val LOCATION_UPDATE_TIMEOUT_MS = 10_000L
        const val LOCATION_MAX_AGE_MS = 5 * 60 * 1_000L
    }
}

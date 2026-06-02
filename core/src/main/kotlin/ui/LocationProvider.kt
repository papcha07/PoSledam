package ui

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationProvider(
    context: Context
) {

    private val client = LocationServices.getFusedLocationProviderClient(context)
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? = suspendCancellableCoroutine { cont ->
        val token = object : CancellationToken() {
            override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken = this
            override fun isCancellationRequested(): Boolean = false
        }

        client.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            token
        ).addOnSuccessListener { location ->
            cont.resume(location)
        }.addOnFailureListener {
            cont.resume(null)
        }
    }
}
package domain.interactor.location

import SendResult
import android.util.Log
import domain.repository.LocationRepository
import domain.user.UserInteractor
import ui.LocationProvider

class LocationInteractorImpl(
    private val locationProvider: LocationProvider,
    private val locationRepository: LocationRepository,
    private val userInteractor: UserInteractor
) : LocationInteractor {

    override suspend fun sendCurrentLocation(): LocationSendResult {
        Log.d("USER_LOCATION", "Requesting current location")
        val location = try {
            locationProvider.getCurrentLocation()
        } catch (e: SecurityException) {
            Log.e("USER_LOCATION", "Location permission denied while reading location", e)
            return LocationSendResult.PermissionDenied
        }

        if (location == null) {
            Log.d("USER_LOCATION", "Current location unavailable")
            return LocationSendResult.LocationUnavailable
        }

        Log.d(
            "USER_LOCATION",
            "Current location resolved: lat=${location.latitude}, lon=${location.longitude}"
        )
        userInteractor.updateUserLocation(
            latitude = location.latitude,
            longitude = location.longitude
        )

        val sendResult = locationRepository.sendCurrentLocation(
            latitude = location.latitude,
            longitude = location.longitude
        )

        return when (sendResult) {
            SendResult.Success -> {
                Log.d("USER_LOCATION", "Current location sent to server")
                LocationSendResult.Success
            }

            is SendResult.BadRequest -> {
                Log.d("USER_LOCATION", "Server rejected location update: ${sendResult.message}")
                LocationSendResult.NetworkError(retryable = false)
            }

            is SendResult.Error -> {
                Log.d("USER_LOCATION", "Location update network error: ${sendResult.message}")
                LocationSendResult.NetworkError(retryable = true)
            }
        }
    }
}

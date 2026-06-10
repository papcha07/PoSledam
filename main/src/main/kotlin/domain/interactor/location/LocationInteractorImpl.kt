package domain.interactor.location

import SendResult
import domain.repository.LocationRepository
import domain.user.UserInteractor
import ui.LocationProvider

class LocationInteractorImpl(
    private val locationProvider: LocationProvider,
    private val locationRepository: LocationRepository,
    private val userInteractor: UserInteractor
) : LocationInteractor {

    override suspend fun sendCurrentLocation(): LocationSendResult {
        val location = try {
            locationProvider.getCurrentLocation()
        } catch (e: SecurityException) {
            return LocationSendResult.PermissionDenied
        }

        if (location == null) {
            return LocationSendResult.LocationUnavailable
        }

        userInteractor.updateUserLocation(
            latitude = location.latitude,
            longitude = location.longitude
        )

        val sendResult = locationRepository.sendCurrentLocation(
            latitude = location.latitude,
            longitude = location.longitude
        )

        return when (sendResult) {
            SendResult.Success -> LocationSendResult.Success

            is SendResult.BadRequest -> LocationSendResult.NetworkError(retryable = false)
            is SendResult.Error -> LocationSendResult.NetworkError(retryable = true)
        }
    }
}

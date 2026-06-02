package domain.interactor.location

import domain.repository.LocationRepository
import domain.user.UserInteractor
import ui.LocationProvider

class LocationInteractorImpl(
    private val locationProvider: LocationProvider,
    private val locationRepository: LocationRepository,
    private val userInteractor: UserInteractor
) : LocationInteractor {

    override suspend fun sendCurrentLocation() {
        val location = locationProvider.getCurrentLocation()
        if (location != null) {
            locationRepository.sendCurrentLocation(
                latitude = location.latitude,
                longitude = location.longitude
            )
            userInteractor.updateUserLocation(
                latitude = location.latitude,
                longitude = location.longitude
            )
        }
    }
}
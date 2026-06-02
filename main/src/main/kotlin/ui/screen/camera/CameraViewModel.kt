package ui.screen.camera

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.interactor.street.StreetPetInteractor
import domain.models.AdvertInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.geo.AddressSuggestion
import ui.LocationProvider
import ui.other.timeUtils.DateTimeUtils
import yandex_core.NetworkResource
import yandex_core.YandexInteractor

class CameraViewModel(
    private val yandexInteractor: YandexInteractor,
    private val locationProvider: LocationProvider,
    private val streetInteractor: StreetPetInteractor
) : ViewModel() {

    private val _uris = MutableStateFlow<List<Uri>>(emptyList())
    val uris = _uris.asStateFlow()

    private val _advertState = MutableStateFlow(AdvertInfo())
    val advertState = _advertState.asStateFlow()

    fun addPhoto(uri: Uri) {
        _uris.value += uri
        _advertState.update { advert ->
            advert.copy(images = _uris.value)
        }
    }

    fun removePhoto(uri: Uri) {
        _uris.update { list -> list.filterNot { it == uri } }
        _advertState.update { advert ->
            advert.copy(images = _uris.value)
        }
    }


    fun addDescription(description: String) {
        _advertState.update {
            it.copy(placeDescription = description)
        }
    }

    private fun setAddress(address: String) {
        _advertState.update {
            it.copy(address = address)
        }
    }

    fun clearViewModel() {
        _advertState.update {
            AdvertInfo()
        }
        _uris.update {
            listOf()
        }
    }


    fun getAddressList(lon: Double, lat: Double) {
        viewModelScope.launch {
            val result = yandexInteractor.resolvePointOnceOne(lon = lon, lat = lat)
            when (result) {
                is NetworkResource.Failed<*> -> {
                    Log.d("resolvePoint", "Error")
                }

                is NetworkResource.Success<AddressSuggestion> -> {
                    Log.d("resolvePoint", result.data.address)
                    setAddress(result.data.address)
                }
            }
        }
    }


    private fun updateEventDateToNow() {
        val currentDate = DateTimeUtils.getCurrentDeviceUiDate()
        _advertState.update {
            it.copy(eventDate = currentDate)
        }
    }


    fun loadMyLocation() {
        viewModelScope.launch {
            val location = locationProvider.getCurrentLocation()
            if (location != null) {
                _advertState.update {
                    it.copy(lat = location.latitude, lon = location.longitude)
                }
                getAddressList(lon = location.longitude, lat = location.latitude)
            }
        }
    }


    fun createStreetAdvert() {
        viewModelScope.launch {
            _advertState.update {
                it.copy(
                    isLoading = true,
                    isPlaced = false,
                    internetError = false,
                    serverError = false
                )
            }
            val response = streetInteractor.createStreetAdvert(_advertState.value)
            when (response) {
                200 -> {
                    _advertState.update {
                        it.copy(isPlaced = true, isLoading = false)
                    }
                }

                400 -> {
                    _advertState.update {
                        it.copy(serverError = true, isLoading = false)
                    }
                }

                -1 -> {
                    _advertState.update {
                        it.copy(internetError = true, isLoading = false)
                    }
                }
            }
        }
    }

    init {
        updateEventDateToNow()
    }


}
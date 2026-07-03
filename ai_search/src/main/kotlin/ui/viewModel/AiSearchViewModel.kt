package ui.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import domain.interactor.AiSearchInteractor
import domain.models.AiSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.LocationProvider
import ui.model.Response
import ui.models.AiChatMessage
import ui.models.ChatRole

class AiSearchViewModel(
    private val interactor: AiSearchInteractor,
    private val locationProvider: LocationProvider
) : ViewModel() {

    /** Приветственные реплики помощника — статичная «шапка» диалога. */
    val greeting: List<AiChatMessage> = listOf(
        AiChatMessage(
            role = ChatRole.Assistant,
            text = "Привет! Отправьте фото пропажи, и умный поиск найдёт вашего питомца, " +
                "если он размещён здесь."
        ),
        AiChatMessage(
            role = ChatRole.Assistant,
            text = "Сейчас можно загрузить одну фотографию. Я сообщу, когда найду похожее объявление."
        )
    )

    /** Живая часть диалога — сообщения в ответ на действия пользователя. */
    private val _conversation = MutableStateFlow<List<AiChatMessage>>(emptyList())
    val conversation: StateFlow<List<AiChatMessage>> = _conversation.asStateFlow()

    /** Выбранное, но ещё не подтверждённое фото (превью справа + кнопки внизу). */
    private val _selectedPhoto = MutableStateFlow<Uri?>(null)
    val selectedPhoto: StateFlow<Uri?> = _selectedPhoto.asStateFlow()

    /** Идёт отправка (POST /api/search/request). */
    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    /** Запрос успешно создан — ждём результат через push. */
    private val _waitingResult = MutableStateFlow(false)
    val waitingResult: StateFlow<Boolean> = _waitingResult.asStateFlow()

    /** История поисков (Paging 3, пагинация по lastDateTime). */
    val history: Flow<PagingData<AiSearchResult>> =
        interactor.loadSearchHistory().cachedIn(viewModelScope)

    /** Выбор фото — строго одно (перевыбор заменяет предыдущее). */
    fun onPhotoSelected(uri: Uri) {
        _selectedPhoto.value = uri
        _waitingResult.value = false
    }

    fun clearPhoto() {
        _selectedPhoto.value = null
    }

    /** «Подтвердить» — отправляем выбранное фото. */
    fun confirm() {
        val uri = _selectedPhoto.value ?: return
        if (_isSending.value) return

        // Фото переходит из «превью» в постоянное сообщение пользователя.
        append(AiChatMessage(role = ChatRole.User, imageUri = uri))
        _selectedPhoto.value = null

        val loadingId = append(
            AiChatMessage(role = ChatRole.Loading, text = "Чуть-чуть подождите!")
        )
        _isSending.value = true

        viewModelScope.launch {
            val location = locationProvider.getCurrentLocation()
            if (location == null) {
                replace(
                    loadingId,
                    AiChatMessage(
                        role = ChatRole.System,
                        text = "Не удалось определить местоположение. Проверьте геолокацию и попробуйте ещё раз."
                    )
                )
                _isSending.value = false
                return@launch
            }

            val response = interactor.createSearchRequest(
                imageUri = uri,
                latitude = location.latitude,
                longitude = location.longitude
            )
            _isSending.value = false

            when (response) {
                Response.SUCCESS -> {
                    replace(
                        loadingId,
                        AiChatMessage(
                            role = ChatRole.Assistant,
                            text = "Фото отправлено. Я начал поиск и пришлю уведомление, когда результат будет готов."
                        )
                    )
                    _waitingResult.value = true
                }

                Response.INTERNET_ERROR -> replace(
                    loadingId,
                    AiChatMessage(
                        role = ChatRole.System,
                        text = "Не удалось отправить фото. Проверьте подключение к интернету."
                    )
                )

                Response.SERVER_ERROR -> replace(
                    loadingId,
                    AiChatMessage(
                        role = ChatRole.System,
                        text = "Не удалось создать запрос. Попробуйте выбрать другое фото."
                    )
                )
            }
        }
    }

    private fun append(message: AiChatMessage): String {
        _conversation.update { it + message }
        return message.id
    }

    private fun replace(id: String, message: AiChatMessage) {
        _conversation.update { list -> list.map { if (it.id == id) message else it } }
    }
}

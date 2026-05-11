package domain.interactor.main

import ApiResponse
import android.util.Log
import apiService.models.auth_models.UpdateUserInfoRequest
import domain.UserInfo
import domain.repository.MainRepository
import model.auth.response.Contact
import ui.model.UserDataUiInfo
import ui.other.Converter

class MainInteractorImpl(
    private val mainRepository: MainRepository,
    private val converter: Converter
) : MainInteractor {

    override suspend fun getUserFromCache(): UserDataUiInfo? {
        val cachedUser = mainRepository.getUserFromCache()
        if (cachedUser != null) return cachedUser.toUserDataInfo() else return null
    }

    override suspend fun syncUserFromServer(): ApiResponse<UserDataUiInfo> {
        return when (val result = mainRepository.syncUserFromServer()) {
            is ApiResponse.Error -> {
                ApiResponse.Error(404)
            }

            is ApiResponse.Success<UserInfo> -> {
                ApiResponse.Success(result.data.toUserDataInfo())
            }
        }
    }

    override suspend fun deleteUser() = mainRepository.deleteUser()

    override suspend fun updateUserInfo(userDataInfo: UserDataUiInfo) {
        mainRepository.updateUserInfo(
            updateUserInfoRequest = userDataInfo.toUpdateUserInfoRequest(),
            userInfo = userDataInfo.toUserInfo()
        )
        Log.d("updateUserInfoInt", userDataInfo.uri.toString())
    }

    override suspend fun updateUserImage(uri: String, id: String) {
        val file = converter.convertToFile(uri)
        mainRepository.updateUserImage(file, id)
    }


    fun UserDataUiInfo.toUserInfo(): UserInfo {

        val contactsOrNull: List<Contact>? = contacts
            .filter { it.url.isNotBlank() }
            .map { it.toContact() }
            .takeIf { it.isNotEmpty() }

        return UserInfo(
            id = id,
            firstName = name,
            avatarPath = uri,
            description = description.ifBlank { null },
            contacts = contactsOrNull
        )
    }

    fun UserDataUiInfo.ContactType.toContact(): Contact =
        Contact(
            contactType = contactType,
            url = url
        )


    fun UserInfo.toUserDataInfo(): UserDataUiInfo {
        return UserDataUiInfo(
            id = id,
            name = firstName,
            description = description.orEmpty(),
            contacts = contacts
                ?.map { it.toContactType() }
                ?: emptyList(),
            uri = avatarPath
        )
    }

    fun Contact.toContactType(): UserDataUiInfo.ContactType {
        return UserDataUiInfo.ContactType(
            contactType = contactType,
            url = url
        )
    }

    fun UserDataUiInfo.toUpdateUserInfoRequest(): UpdateUserInfoRequest {
        val contactsOrNull: List<Contact>? = contacts
            .filter { it.url.isNotBlank() }   // убираем пустые
            .map { it.toContact() }           // теперь List<Contact>
            .takeIf { it.isNotEmpty() }       // если пусто → null

        return UpdateUserInfoRequest(
            id = id,
            firstName = name,
            description = description.ifBlank { null },
            contacts = contactsOrNull
        )
    }


}
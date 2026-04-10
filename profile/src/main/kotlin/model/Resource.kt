package model

sealed class Resource<out T> {
    data object Success : Resource<Nothing>()
    data class Failed(val message: InternetStatus) : Resource<Nothing>()
}


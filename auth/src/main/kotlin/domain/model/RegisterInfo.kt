package domain.model

data class RegisterInfo(
    val email: String,
    val password : String,
    val firstName : String,
    val description : String,
    val uri: String? = null
)

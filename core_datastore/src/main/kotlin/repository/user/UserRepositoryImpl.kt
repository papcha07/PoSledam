package repository.user

import ApiResponse
import apiService.AuthService
import apiService.models.auth_models.UserInfoResponse
import db.user.UserDao
import domain.user.model.User
import domain.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import toDomain
import toEntity

class UserRepositoryImpl(
    private val authApi: AuthService,
    private val userDao: UserDao
) : UserRepository {

    override suspend fun observeUser(): Flow<User> {
        return userDao.observeUser().map { entity ->
            entity.toDomain()
        }
    }


    override suspend fun refreshUser() {
        val remoteUser = authApi.getInfo()
        when (remoteUser) {

            is ApiResponse.Error -> {

            }

            is ApiResponse.Success<UserInfoResponse> -> {
                val responseUserModel = remoteUser.data
                val entityUser = responseUserModel.toEntity()
                userDao.saveUser(entityUser)
            }
        }
    }

}
package repository.user

import ApiResponse
import apiService.AuthService
import apiService.models.auth_models.UserInfoResponse
import db.user.UserDao
import domain.user.UserRepository
import domain.user.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import toDomain
import toEntity
import toUpdateUserInfoRequest
import toUserEntity
import withIo

class UserRepositoryImpl(
    private val authApi: AuthService,
    private val userDao: UserDao
) : UserRepository {

    override suspend fun updateUser(user: User) {
        withIo {
            userDao.updateUserInfo(user.toUserEntity())
        }
        authApi.updateUserInfo(user.toUpdateUserInfoRequest())
    }

    override fun observeUser(): Flow<User?> {
        return userDao.observeUser().map { entity ->
            entity?.toDomain()
        }
    }


    override suspend fun refreshUser() {
        val remoteUser = authApi.getInfo()
        when (remoteUser) {
            is ApiResponse.Error -> {}

            is ApiResponse.Success<UserInfoResponse> -> {
                val responseUserModel = remoteUser.data
                val entityUser = responseUserModel.toEntity()
                withIo {
                    userDao.saveUser(entityUser)
                }
            }
        }
    }

    override suspend fun clearUser() {
        withIo {
            userDao.clearUser()
        }
    }

    override suspend fun updateUserLocation(latitude: Double, longitude: Double) {
        withIo {
             userDao.observeUser().collect {
                userEntity ->
                if(userEntity != null){
                    userDao.updateUserLocation(
                        userEntity.id,
                        latitude,
                        longitude
                    )
                }
            }
        }
    }

}
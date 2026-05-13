import androidx.room.Room
import db.notification.NotificationDao
import db.notification.NotificationDatabase
import db.user.UserDao
import db.user.UserDatabase
import domain.notification.NotificationInteractor
import domain.notification.NotificationInteractorImpl
import domain.notification.NotificationSettingsInteractor
import domain.notification.NotificationSettingsInteractorImpl
import domain.user.UserInteractor
import domain.user.UserInteractorImpl
import domain.user.UserRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import repository.notification.NotificationRepository
import repository.notification.NotificationRepositoryImpl
import repository.notification.NotificationSettingsRepository
import repository.notification.NotificationSettingsRepositoryImpl
import repository.user.UserRepositoryImpl

val dataStoreModule = module {

    single<NotificationDatabase> {
        Room.databaseBuilder(
            androidApplication(),
            NotificationDatabase::class.java,
            "notification.db"
        ).build()
    }

    single<NotificationDao> {
        get<NotificationDatabase>().notificationDao()
    }

    single<NotificationRepository> {
        NotificationRepositoryImpl(get())
    }

    single<NotificationInteractor> {
        NotificationInteractorImpl(get())
    }

    single<NotificationSettingsRepository> {
        NotificationSettingsRepositoryImpl(androidApplication())
    }

    single<NotificationSettingsInteractor> {
        NotificationSettingsInteractorImpl(get())
    }

    single<UserDatabase> {
        Room.databaseBuilder(
            androidApplication(),
            UserDatabase::class.java,
            "user.db"
        ).build()
    }

    single<UserDao> {
        get<UserDatabase>().userDao()
    }

    single<UserRepository> {
        UserRepositoryImpl(get(), get())
    }

    single<UserInteractor> {
        UserInteractorImpl(get(), get())
    }

}
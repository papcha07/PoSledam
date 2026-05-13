import androidx.room.Room
import db.notification.NotificationDao
import db.notification.NotificationDatabase
import domain.notification.NotificationInteractor
import domain.notification.NotificationInteractorImpl
import domain.notification.NotificationSettingsInteractor
import domain.notification.NotificationSettingsInteractorImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import repository.notification.NotificationRepository
import repository.notification.NotificationRepositoryImpl
import repository.notification.NotificationSettingsRepository
import repository.notification.NotificationSettingsRepositoryImpl

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

}
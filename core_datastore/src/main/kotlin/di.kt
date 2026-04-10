import androidx.room.Room
import db.NotificationDao
import db.NotificationDatabase
import domain.NotificationInteractor
import domain.NotificationInteractorImpl
import domain.NotificationSettingsInteractor
import domain.NotificationSettingsInteractorImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import repository.NotificationRepository
import repository.NotificationRepositoryImpl
import repository.NotificationSettingsRepository
import repository.NotificationSettingsRepositoryImpl

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
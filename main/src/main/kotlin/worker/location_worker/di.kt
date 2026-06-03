package worker.location_worker

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun getWorkerModule() = module {
    single<WorkerInteractor> {
        WorkerInteractorImpl(androidContext())
    }
}
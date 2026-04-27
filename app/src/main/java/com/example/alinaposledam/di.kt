package com.example.alinaposledam

import com.example.alinaposledam.worker.WorkerInteractor
import com.example.alinaposledam.worker.WorkerInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun getWorkerModule() = module {
    single<WorkerInteractor> {
        WorkerInteractorImpl(androidContext())
    }
}
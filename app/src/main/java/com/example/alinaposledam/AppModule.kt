package com.example.alinaposledam

import com.example.alinaposledam.firebase.FirebaseTokenProvider
import org.koin.dsl.module

val appModule = module {
    single { FirebaseTokenProvider(authService = get()) }
}
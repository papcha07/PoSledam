package com.example.alinaposledam.worker

interface WorkerRepository {
    suspend fun sendLocation()
}
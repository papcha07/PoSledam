package com.example.alinaposledam

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import kotlinx.coroutines.flow.MutableSharedFlow


class MainActivity : ComponentActivity() {

    private val notificationIntents = MutableSharedFlow<Intent>(
        extraBufferCapacity = 1
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppNavGraph(
                initialIntent = intent,
                notificationIntents = notificationIntents
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        setIntent(intent)
        notificationIntents.tryEmit(intent)
    }
}










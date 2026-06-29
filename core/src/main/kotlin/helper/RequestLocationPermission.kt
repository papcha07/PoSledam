package helper

import android.Manifest
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun RequestLocationPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    onPermissionPermanentlyDenied: () -> Unit = {},
    requestBackgroundPermission: Boolean = false,
    onBackgroundPermissionGranted: () -> Unit = {},
    onBackgroundPermissionDenied: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var showForegroundDeniedDialog by remember { mutableStateOf(false) }
    var showForegroundSettingsDialog by remember { mutableStateOf(false) }
    var showBackgroundRationaleDialog by remember { mutableStateOf(false) }

    fun handleForegroundGranted() {
        Log.d("USER_LOCATION", "Foreground location permission granted")
        onPermissionGranted()
        if (!requestBackgroundPermission) return

        if (context.hasBackgroundLocationPermission()) {
            Log.d("WORKER_MANAGER", "Background location permission already granted")
            onBackgroundPermissionGranted()
            return
        }

        if (!context.wasBackgroundLocationPermissionRequested()) {
            Log.d("WORKER_MANAGER", "Background location permission rationale requested")
            showBackgroundRationaleDialog = true
        } else {
            Log.d("WORKER_MANAGER", "Background location permission is not granted")
            onBackgroundPermissionDenied()
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            Log.d("USER_LOCATION", "Foreground location permission request result: granted")
            handleForegroundGranted()
        } else if (context.isForegroundLocationPermanentlyDenied()) {
            Log.d("USER_LOCATION", "Foreground location permission request result: permanently denied")
            showForegroundSettingsDialog = true
            onPermissionPermanentlyDenied()
        } else {
            Log.d("USER_LOCATION", "Foreground location permission request result: denied")
            showForegroundDeniedDialog = true
            onPermissionDenied()
        }
    }

    val backgroundLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        context.markBackgroundLocationPermissionRequested()
        if (isGranted) {
            Log.d("WORKER_MANAGER", "Background location permission request result: granted")
            onBackgroundPermissionGranted()
        } else {
            Log.d("WORKER_MANAGER", "Background location permission request result: denied")
            onBackgroundPermissionDenied()
        }
    }

    fun requestForegroundPermission() {
        Log.d("USER_LOCATION", "Requesting foreground location permission")
        context.markForegroundLocationPermissionRequested()
        launcher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    LaunchedEffect(Unit) {
        Log.d(
            "USER_LOCATION",
            "Checking foreground location permission: has=${context.hasForegroundLocationPermission()}, requested=${context.wasForegroundLocationPermissionRequested()}, permanent=${context.isForegroundLocationPermanentlyDenied()}"
        )
        if (context.hasForegroundLocationPermission()) {
            handleForegroundGranted()
        } else if (!context.wasForegroundLocationPermissionRequested()) {
            requestForegroundPermission()
        } else if (context.isForegroundLocationPermanentlyDenied()) {
            Log.d("USER_LOCATION", "Foreground location permission is permanently denied")
            showForegroundSettingsDialog = true
            onPermissionPermanentlyDenied()
        } else {
            Log.d("USER_LOCATION", "Foreground location permission is denied, showing retry dialog")
            showForegroundDeniedDialog = true
            onPermissionDenied()
        }
    }

    DisposableEffect(lifecycleOwner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && context.hasForegroundLocationPermission()) {
                Log.d("USER_LOCATION", "ON_RESUME with foreground location permission granted")
                handleForegroundGranted()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (showForegroundDeniedDialog) {
        AlertDialog(
            onDismissRequest = { showForegroundDeniedDialog = false },
            title = { Text("Нужна геолокация") },
            text = {
                Text("Разрешите доступ к геолокации, чтобы приложение могло отправить текущую позицию и показывать питомцев рядом.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showForegroundDeniedDialog = false
                        requestForegroundPermission()
                    }
                ) {
                    Text("Повторить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showForegroundDeniedDialog = false }) {
                    Text("Позже")
                }
            }
        )
    }

    if (showForegroundSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showForegroundSettingsDialog = false },
            title = { Text("Геолокация отключена") },
            text = {
                Text("Доступ к геолокации отключён в настройках приложения. Откройте настройки, чтобы разрешить доступ.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showForegroundSettingsDialog = false
                        context.openAppSettings()
                    }
                ) {
                    Text("Настройки")
                }
            },
            dismissButton = {
                TextButton(onClick = { showForegroundSettingsDialog = false }) {
                    Text("Позже")
                }
            }
        )
    }

    if (showBackgroundRationaleDialog) {
        val backgroundPermissionLabel = remember {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.packageManager.backgroundPermissionOptionLabel.toString()
            } else {
                "Разрешить всегда"
            }
        }
        AlertDialog(
            onDismissRequest = {
                showBackgroundRationaleDialog = false
                context.markBackgroundLocationPermissionRequested()
                onBackgroundPermissionDenied()
            },
            title = { Text("Фоновая геолокация") },
            text = {
                Text("Периодическая отправка геолокации работает, когда приложение не открыто. Для этого в настройках геолокации выберите \"$backgroundPermissionLabel\".")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showBackgroundRationaleDialog = false
                        context.markBackgroundLocationPermissionRequested()
                        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                            backgroundLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        } else {
                            context.openAppSettings()
                        }
                    }
                ) {
                    Text("Продолжить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showBackgroundRationaleDialog = false
                        context.markBackgroundLocationPermissionRequested()
                        onBackgroundPermissionDenied()
                    }
                ) {
                    Text("Не сейчас")
                }
            }
        )
    }
}

@Composable
fun RequestNotificationPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val context = LocalContext.current
    var showNotificationDeniedDialog by remember { mutableStateOf(false) }
    var showNotificationSettingsDialog by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        context.markNotificationPermissionRequested()
        if (isGranted) {
            onPermissionGranted()
        } else {
            if (context.isNotificationPermissionPermanentlyDenied()) {
                showNotificationSettingsDialog = true
            } else {
                showNotificationDeniedDialog = true
            }
            onPermissionDenied()
        }
    }

    fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            onPermissionGranted()
            return
        }

        if (context.hasNotificationPermission()) {
            onPermissionGranted()
        } else if (context.isNotificationPermissionPermanentlyDenied()) {
            showNotificationSettingsDialog = true
            onPermissionDenied()
        } else {
            context.markNotificationPermissionRequested()
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(Unit) {
        requestNotificationPermission()
    }

    if (showNotificationDeniedDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationDeniedDialog = false },
            title = { Text("Нужны уведомления") },
            text = {
                Text("Разрешите уведомления, чтобы получать важные сообщения об объявлениях и событиях сервиса.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showNotificationDeniedDialog = false
                        requestNotificationPermission()
                    }
                ) {
                    Text("Повторить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotificationDeniedDialog = false }) {
                    Text("Позже")
                }
            }
        )
    }

    if (showNotificationSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationSettingsDialog = false },
            title = { Text("Уведомления отключены") },
            text = {
                Text("Доступ к уведомлениям отключён в настройках приложения. Откройте настройки, чтобы разрешить уведомления.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showNotificationSettingsDialog = false
                        context.openAppSettings()
                    }
                ) {
                    Text("Настройки")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotificationSettingsDialog = false }) {
                    Text("Позже")
                }
            }
        )
    }
}

package helper

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
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
import androidx.core.content.ContextCompat
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
        onPermissionGranted()
        if (!requestBackgroundPermission) return

        if (context.hasBackgroundLocationPermission()) {
            onBackgroundPermissionGranted()
            return
        }

        if (!context.wasBackgroundLocationPermissionRequested()) {
            showBackgroundRationaleDialog = true
        } else {
            onBackgroundPermissionDenied()
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            handleForegroundGranted()
        } else if (context.isForegroundLocationPermanentlyDenied()) {
            showForegroundSettingsDialog = true
            onPermissionPermanentlyDenied()
        } else {
            showForegroundDeniedDialog = true
            onPermissionDenied()
        }
    }

    val backgroundLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        context.markBackgroundLocationPermissionRequested()
        if (isGranted) {
            onBackgroundPermissionGranted()
        } else {
            onBackgroundPermissionDenied()
        }
    }

    fun requestForegroundPermission() {
        context.markForegroundLocationPermissionRequested()
        launcher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    LaunchedEffect(Unit) {
        if (context.hasForegroundLocationPermission()) {
            handleForegroundGranted()
        } else if (!context.wasForegroundLocationPermissionRequested()) {
            requestForegroundPermission()
        } else if (context.isForegroundLocationPermanentlyDenied()) {
            showForegroundSettingsDialog = true
            onPermissionPermanentlyDenied()
        } else {
            showForegroundDeniedDialog = true
            onPermissionDenied()
        }
    }

    DisposableEffect(lifecycleOwner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && context.hasForegroundLocationPermission()) {
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

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            onPermissionGranted()
            return@LaunchedEffect
        }

        val permissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (permissionGranted) {
            onPermissionGranted()
        } else {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

private fun Context.openAppSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

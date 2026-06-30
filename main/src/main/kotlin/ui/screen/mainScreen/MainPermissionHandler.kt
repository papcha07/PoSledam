package ui.screen.mainScreen

import android.Manifest
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
import helper.hasForegroundLocationPermission
import helper.hasNotificationPermission
import helper.isForegroundLocationPermanentlyDenied
import helper.isNotificationPermissionPermanentlyDenied
// import helper.hasBackgroundLocationPermission
// import helper.markBackgroundLocationPermissionRequested
import helper.markForegroundLocationPermissionRequested
import helper.markNotificationPermissionRequested
import helper.openAppSettings
import helper.shouldShowForegroundLocationRationale
import helper.shouldShowNotificationRationale
// import helper.wasBackgroundLocationPermissionRequested

@Composable
fun MainPermissionHandler(
    viewModel: MainScreenViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var showLocationRationaleDialog by remember { mutableStateOf(false) }
    var showLocationSettingsDialog by remember { mutableStateOf(false) }
    // Background location is temporarily disabled for moderation.
    // var showBackgroundLocationRationaleDialog by remember { mutableStateOf(false) }
    var showNotificationRationaleDialog by remember { mutableStateOf(false) }
    var showNotificationSettingsDialog by remember { mutableStateOf(false) }
    var settingsTarget by remember { mutableStateOf<PermissionTarget?>(null) }
    var locationStepFinished by remember { mutableStateOf(false) }
    var notificationStepFinished by remember { mutableStateOf(false) }

    lateinit var requestNotificationPermission: () -> Unit
    lateinit var checkNotificationPermission: () -> Unit
    lateinit var finishLocationStep: () -> Unit
    lateinit var handleForegroundLocationGranted: () -> Unit
    lateinit var requestForegroundLocationPermission: () -> Unit

    fun finishPermissionFlow() {
        if (notificationStepFinished) return
        notificationStepFinished = true
        Log.d("MAIN_PERMISSIONS", "Permission flow finished")
    }

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            handleForegroundLocationGranted()
        } else if (context.isForegroundLocationPermanentlyDenied()) {
            viewModel.onForegroundLocationPermissionPermanentlyDenied()
            showLocationSettingsDialog = true
        } else {
            viewModel.onForegroundLocationPermissionDenied()
            showLocationRationaleDialog = true
        }
    }

    // Background location is temporarily disabled for moderation.
    // val backgroundLocationLauncher = rememberLauncherForActivityResult(
    //     contract = ActivityResultContracts.RequestPermission()
    // ) { isGranted ->
    //     context.markBackgroundLocationPermissionRequested()
    //     if (isGranted) {
    //         viewModel.onBackgroundLocationPermissionGranted()
    //     } else {
    //         viewModel.onBackgroundLocationPermissionDenied()
    //     }
    //     finishLocationStep()
    // }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        context.markNotificationPermissionRequested()
        if (isGranted) {
            Log.d("MAIN_PERMISSIONS", "Notification permission granted")
            finishPermissionFlow()
        } else if (context.isNotificationPermissionPermanentlyDenied()) {
            Log.d("MAIN_PERMISSIONS", "Notification permission permanently denied")
            showNotificationSettingsDialog = true
        } else {
            Log.d("MAIN_PERMISSIONS", "Notification permission denied")
            showNotificationRationaleDialog = true
        }
    }

    requestNotificationPermission = {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            finishPermissionFlow()
        } else {
            context.markNotificationPermissionRequested()
            Log.d("MAIN_PERMISSIONS", "Requesting notification permission")
            notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    checkNotificationPermission = {
        if (notificationStepFinished) {
            // Nothing to do.
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            Log.d("MAIN_PERMISSIONS", "Notification permission is not required")
            finishPermissionFlow()
        } else if (context.hasNotificationPermission()) {
            Log.d("MAIN_PERMISSIONS", "Notification permission already granted")
            finishPermissionFlow()
        } else if (context.isNotificationPermissionPermanentlyDenied()) {
            Log.d("MAIN_PERMISSIONS", "Notification permission is permanently denied")
            showNotificationSettingsDialog = true
        } else if (context.shouldShowNotificationRationale()) {
            Log.d("MAIN_PERMISSIONS", "Showing notification rationale")
            showNotificationRationaleDialog = true
        } else {
            requestNotificationPermission()
        }
    }

    finishLocationStep = {
        if (!locationStepFinished) {
            locationStepFinished = true
            checkNotificationPermission()
        }
    }

    // Background location is temporarily disabled for moderation.
    // fun handleBackgroundLocation() {
    //     if (context.hasBackgroundLocationPermission()) {
    //         viewModel.onBackgroundLocationPermissionGranted()
    //         finishLocationStep()
    //     } else if (!context.wasBackgroundLocationPermissionRequested()) {
    //         showBackgroundLocationRationaleDialog = true
    //     } else {
    //         viewModel.onBackgroundLocationPermissionDenied()
    //         finishLocationStep()
    //     }
    // }

    handleForegroundLocationGranted = {
        Log.d("USER_LOCATION", "Foreground location permission granted")
        viewModel.onForegroundLocationPermissionGranted()
        // Background location is temporarily disabled for moderation.
        // handleBackgroundLocation()
        finishLocationStep()
    }

    requestForegroundLocationPermission = {
        context.markForegroundLocationPermissionRequested()
        Log.d("USER_LOCATION", "Requesting foreground location permission")
        locationLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    fun checkForegroundLocationPermission() {
        if (locationStepFinished) return

        Log.d(
            "USER_LOCATION",
            "Checking foreground location permission: has=${context.hasForegroundLocationPermission()}, permanent=${context.isForegroundLocationPermanentlyDenied()}"
        )

        if (context.hasForegroundLocationPermission()) {
            handleForegroundLocationGranted()
        } else if (context.isForegroundLocationPermanentlyDenied()) {
            viewModel.onForegroundLocationPermissionPermanentlyDenied()
            showLocationSettingsDialog = true
        } else if (context.shouldShowForegroundLocationRationale()) {
            viewModel.onForegroundLocationPermissionDenied()
            showLocationRationaleDialog = true
        } else {
            requestForegroundLocationPermission()
        }
    }

    LaunchedEffect(Unit) {
        if (viewModel.consumePermissionFlowLaunch()) {
            checkForegroundLocationPermission()
        }
    }

    DisposableEffect(lifecycleOwner, settingsTarget) {
        val observer = LifecycleEventObserver { _, event ->
            if (event != Lifecycle.Event.ON_RESUME || settingsTarget == null) return@LifecycleEventObserver

            when (settingsTarget) {
                PermissionTarget.Location -> {
                    settingsTarget = null
                    if (context.hasForegroundLocationPermission()) {
                        handleForegroundLocationGranted()
                    } else {
                        viewModel.onForegroundLocationPermissionDenied()
                        finishLocationStep()
                    }
                }

                // Background location is temporarily disabled for moderation.
                // PermissionTarget.BackgroundLocation -> {
                //     settingsTarget = null
                //     if (context.hasBackgroundLocationPermission()) {
                //         viewModel.onBackgroundLocationPermissionGranted()
                //     } else {
                //         viewModel.onBackgroundLocationPermissionDenied()
                //     }
                //     finishLocationStep()
                // }

                PermissionTarget.Notification -> {
                    settingsTarget = null
                    finishPermissionFlow()
                }

                null -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (showLocationRationaleDialog) {
        LocationPermissionDialog(
            confirmText = "Разрешить",
            onConfirm = {
                showLocationRationaleDialog = false
                requestForegroundLocationPermission()
            },
            onDismiss = {
                showLocationRationaleDialog = false
                finishLocationStep()
            }
        )
    }

    if (showLocationSettingsDialog) {
        LocationPermissionDialog(
            confirmText = "Открыть настройки",
            onConfirm = {
                showLocationSettingsDialog = false
                settingsTarget = PermissionTarget.Location
                context.openAppSettings()
            },
            onDismiss = {
                showLocationSettingsDialog = false
                finishLocationStep()
            }
        )
    }

    // Background location is temporarily disabled for moderation.
    // if (showBackgroundLocationRationaleDialog) {
    //     val backgroundPermissionLabel = remember {
    //         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
    //             context.packageManager.backgroundPermissionOptionLabel.toString()
    //         } else {
    //             "Разрешить всегда"
    //         }
    //     }
    //
    //     AlertDialog(
    //         onDismissRequest = {
    //             showBackgroundLocationRationaleDialog = false
    //             context.markBackgroundLocationPermissionRequested()
    //             viewModel.onBackgroundLocationPermissionDenied()
    //             finishLocationStep()
    //         },
    //         title = { Text("Фоновая геолокация") },
    //         text = {
    //             Text("Периодическая отправка геолокации работает, когда приложение не открыто. Для этого в настройках геолокации выберите \"$backgroundPermissionLabel\".")
    //         },
    //         confirmButton = {
    //             TextButton(
    //                 onClick = {
    //                     showBackgroundLocationRationaleDialog = false
    //                     context.markBackgroundLocationPermissionRequested()
    //                     if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
    //                         backgroundLocationLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    //                     } else {
    //                         settingsTarget = PermissionTarget.BackgroundLocation
    //                         context.openAppSettings()
    //                     }
    //                 }
    //             ) {
    //                 Text("Продолжить")
    //             }
    //         },
    //         dismissButton = {
    //             TextButton(
    //                 onClick = {
    //                     showBackgroundLocationRationaleDialog = false
    //                     context.markBackgroundLocationPermissionRequested()
    //                     viewModel.onBackgroundLocationPermissionDenied()
    //                     finishLocationStep()
    //                 }
    //             ) {
    //                 Text("Позже")
    //             }
    //         }
    //     )
    // }

    if (showNotificationRationaleDialog) {
        NotificationPermissionDialog(
            confirmText = "Разрешить",
            onConfirm = {
                showNotificationRationaleDialog = false
                requestNotificationPermission()
            },
            onDismiss = {
                showNotificationRationaleDialog = false
                finishPermissionFlow()
            }
        )
    }

    if (showNotificationSettingsDialog) {
        NotificationPermissionDialog(
            confirmText = "Открыть настройки",
            onConfirm = {
                showNotificationSettingsDialog = false
                settingsTarget = PermissionTarget.Notification
                context.openAppSettings()
            },
            onDismiss = {
                showNotificationSettingsDialog = false
                finishPermissionFlow()
            }
        )
    }
}

@Composable
private fun LocationPermissionDialog(
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Нужна геолокация") },
        text = {
            Text("Локация нужна, чтобы показывать объявления рядом с вами и помогать быстрее находить питомцев. Вы можете разрешить доступ в настройках.")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Позже")
            }
        }
    )
}

@Composable
private fun NotificationPermissionDialog(
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Нужны уведомления") },
        text = {
            Text("Уведомления нужны, чтобы сообщать, когда вашего питомца заметили или нашли.")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Позже")
            }
        }
    )
}

private enum class PermissionTarget {
    Location,
    // Background location is temporarily disabled for moderation.
    // BackgroundLocation,
    Notification
}

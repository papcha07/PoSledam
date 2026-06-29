package helper

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import androidx.core.content.ContextCompat

fun Context.hasForegroundLocationPermission(): Boolean {
    val fineGranted = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val coarseGranted = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    return fineGranted || coarseGranted
}

fun Context.hasBackgroundLocationPermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

fun Context.wasForegroundLocationPermissionRequested(): Boolean {
    return locationPermissionPreferences()
        .getBoolean(KEY_FOREGROUND_LOCATION_REQUESTED, false)
}

fun Context.markForegroundLocationPermissionRequested() {
    locationPermissionPreferences().edit {
        putBoolean(KEY_FOREGROUND_LOCATION_REQUESTED, true)
    }
}

fun Context.wasBackgroundLocationPermissionRequested(): Boolean {
    return locationPermissionPreferences()
        .getBoolean(KEY_BACKGROUND_LOCATION_REQUESTED, false)
}

fun Context.markBackgroundLocationPermissionRequested() {
    locationPermissionPreferences().edit {
        putBoolean(KEY_BACKGROUND_LOCATION_REQUESTED, true)
    }
}

fun Context.isForegroundLocationPermanentlyDenied(): Boolean {
    return wasForegroundLocationPermissionRequested() &&
            !hasForegroundLocationPermission() &&
            !shouldShowForegroundLocationRationale()
}

fun Context.shouldShowForegroundLocationRationale(): Boolean {
    val activity = findActivity() ?: return false
    return ActivityCompat.shouldShowRequestPermissionRationale(
        activity,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) || ActivityCompat.shouldShowRequestPermissionRationale(
        activity,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
}

fun Context.hasNotificationPermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

fun Context.wasNotificationPermissionRequested(): Boolean {
    return notificationPermissionPreferences()
        .getBoolean(KEY_NOTIFICATION_REQUESTED, false)
}

fun Context.markNotificationPermissionRequested() {
    notificationPermissionPreferences().edit {
        putBoolean(KEY_NOTIFICATION_REQUESTED, true)
    }
}

fun Context.isNotificationPermissionPermanentlyDenied(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            wasNotificationPermissionRequested() &&
            !hasNotificationPermission() &&
            !shouldShowNotificationRationale()
}

fun Context.shouldShowNotificationRationale(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return false
    val activity = findActivity() ?: return false
    return ActivityCompat.shouldShowRequestPermissionRationale(
        activity,
        Manifest.permission.POST_NOTIFICATIONS
    )
}

fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) return currentContext
        currentContext = currentContext.baseContext
    }
    return null
}

fun Context.openAppSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

private fun Context.locationPermissionPreferences() =
    applicationContext.getSharedPreferences(
        LOCATION_PERMISSION_PREFS,
        Context.MODE_PRIVATE
    )

private fun Context.notificationPermissionPreferences() =
    applicationContext.getSharedPreferences(
        NOTIFICATION_PERMISSION_PREFS,
        Context.MODE_PRIVATE
    )

private const val LOCATION_PERMISSION_PREFS = "location_permission_prefs"
private const val NOTIFICATION_PERMISSION_PREFS = "notification_permission_prefs"
private const val KEY_FOREGROUND_LOCATION_REQUESTED = "foreground_location_requested"
private const val KEY_BACKGROUND_LOCATION_REQUESTED = "background_location_requested"
private const val KEY_NOTIFICATION_REQUESTED = "notification_requested"

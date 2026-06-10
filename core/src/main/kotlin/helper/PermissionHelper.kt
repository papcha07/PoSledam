package helper

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
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

fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) return currentContext
        currentContext = currentContext.baseContext
    }
    return null
}

private fun Context.locationPermissionPreferences() =
    applicationContext.getSharedPreferences(
        LOCATION_PERMISSION_PREFS,
        Context.MODE_PRIVATE
    )

private const val LOCATION_PERMISSION_PREFS = "location_permission_prefs"
private const val KEY_FOREGROUND_LOCATION_REQUESTED = "foreground_location_requested"
private const val KEY_BACKGROUND_LOCATION_REQUESTED = "background_location_requested"

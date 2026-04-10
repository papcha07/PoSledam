package ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

/**
 * Самый простой компонент карты:
 * - ставит текущую позицию
 * - плавно приближает камеру
 */
@SuppressLint("MissingPermission")
@Composable
fun ProfileMapSimple(
    modifier: Modifier = Modifier,
    latitude: Double,
    longitude: Double,
    zoom: Float = 16f
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val mapView = remember { MapView(context) }
    var cameraMoved by remember { mutableStateOf(false) }

    DisposableEffect(mapView, lifecycle) {
        val observer = object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                MapKitFactory.getInstance().onStart()
                mapView.onStart()
            }

            override fun onStop(owner: LifecycleOwner) {
                mapView.onStop()
                MapKitFactory.getInstance().onStop()
            }
        }
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {

        AndroidView(
            factory = { mapView },
            update = { view ->
                if (!cameraMoved) {
                    val point = Point(latitude, longitude)
                    view.map.move(
                        CameraPosition(point, zoom, 0f, 0f),
                        Animation(Animation.Type.SMOOTH, 0.7f),
                        null
                    )
                    cameraMoved = true
                }
            }
        )
    }
}

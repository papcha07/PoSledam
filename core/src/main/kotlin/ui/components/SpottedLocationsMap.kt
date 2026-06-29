package ui.components

import android.graphics.Canvas
import android.graphics.PointF
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.createBitmap
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.example.core.R
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import ui.BASE_URL
import ui.theme.textHint

data class SpottedMapPoint(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val spottedBy: String,
    val createdAt: String,
    val imagePaths: List<String>
)

@Composable
fun SpottedLocationsMap(
    modifier: Modifier = Modifier,
    points: List<SpottedMapPoint>
) {
    val context = LocalContext.current
    ensureYandexMapKitInitialized(context)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val mapView = remember { MapView(context) }
    val pinProvider = rememberPinProvider(R.drawable.ic_lapa_point, sizeDp = 36f)

    var selectedPoint by remember { mutableStateOf<SpottedMapPoint?>(null) }
    var collectionRef by remember { mutableStateOf<MapObjectCollection?>(null) }
    var renderedKeys by remember { mutableStateOf<List<String>>(emptyList()) }
    var tapListenersRef by remember { mutableStateOf<List<MapObjectTapListener>>(emptyList()) }
    val selectedPointSetter by rememberUpdatedState<(SpottedMapPoint) -> Unit> { point ->
        selectedPoint = point
    }

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
            collectionRef?.clear()
            collectionRef = null
            tapListenersRef = emptyList()
            lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .testTag("spotted_locations_map")
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                collectionRef = mapView.map.mapObjects.addCollection()
                mapView
            },
            update = { view ->
                val newKeys = points.map { "${it.id}_${it.latitude}_${it.longitude}" }
                if (renderedKeys == newKeys) return@AndroidView

                val collection = collectionRef ?: view.map.mapObjects.addCollection().also {
                    collectionRef = it
                }
                collection.clear()

                val tapListeners = points.map { spottedPoint ->
                    val mapPoint = Point(spottedPoint.latitude, spottedPoint.longitude)
                    val tapListener = object : MapObjectTapListener {
                        override fun onMapObjectTap(
                            mapObject: MapObject,
                            tapPoint: Point
                        ): Boolean {
                            selectedPointSetter(spottedPoint)
                            return true
                        }
                    }

                    collection.addPlacemark(mapPoint).apply {
                        setIcon(pinProvider)
                        setIconStyle(
                            IconStyle().apply {
                                anchor = PointF(0.5f, 1f)
                                scale = 1f
                            }
                        )
                        addTapListener(tapListener)
                    }
                    tapListener
                }

                tapListenersRef = tapListeners
                renderedKeys = newKeys
                moveCameraToPoints(view, points)
            }
        )

        ZoomMenu(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp),
            zoom = {
                val map = mapView.map
                val current = map.cameraPosition
                map.move(
                    CameraPosition(
                        current.target,
                        current.zoom + 0.5f,
                        current.azimuth,
                        current.tilt
                    ),
                    Animation(Animation.Type.SMOOTH, 0.2f),
                    null
                )
            },
            unZoom = {
                val map = mapView.map
                val current = map.cameraPosition
                map.move(
                    CameraPosition(
                        current.target,
                        current.zoom - 0.5f,
                        current.azimuth,
                        current.tilt
                    ),
                    Animation(Animation.Type.SMOOTH, 0.2f),
                    null
                )
            }
        )

        DropdownMenu(
            expanded = selectedPoint != null,
            onDismissRequest = { selectedPoint = null },
            modifier = Modifier
                .widthIn(min = 260.dp, max = 320.dp)
                .background(Color.White)
        ) {
            selectedPoint?.let { point ->
                SpottedPointDropdownContent(point = point)
            }
        }
    }
}

@Composable
private fun SpottedPointDropdownContent(
    point: SpottedMapPoint
) {
    Column(
        modifier = Modifier.padding(12.dp)
    ) {
        Text(
            text = point.spottedBy,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = point.createdAt,
            fontSize = 13.sp,
            color = textHint
        )

        val images = point.imagePaths.filter { it.isNotBlank() }
        if (images.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                images.forEach { imagePath ->
                    AsyncImage(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        model = imagePath.toImageModel(),
                        placeholder = painterResource(R.drawable.ic_dog),
                        error = painterResource(R.drawable.ic_dog),
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

private fun moveCameraToPoints(
    mapView: MapView,
    points: List<SpottedMapPoint>
) {
    if (points.isEmpty()) return

    val firstPoint = points.first()
    if (points.size == 1) {
        mapView.map.move(
            CameraPosition(
                Point(firstPoint.latitude, firstPoint.longitude),
                15f,
                0f,
                0f
            ),
            Animation(Animation.Type.SMOOTH, 0.3f),
            null
        )
        return
    }

    val minLatitude = points.minOf { it.latitude }
    val maxLatitude = points.maxOf { it.latitude }
    val minLongitude = points.minOf { it.longitude }
    val maxLongitude = points.maxOf { it.longitude }

    val cameraPosition = mapView.map.cameraPosition(
        Geometry.fromBoundingBox(
            BoundingBox(
                Point(minLatitude, minLongitude),
                Point(maxLatitude, maxLongitude)
            )
        )
    )
    mapView.post {
        mapView.map.move(cameraPosition)
    }
}

private fun String.toImageModel(): String {
    return when {
        startsWith("http://") || startsWith("https://") || startsWith("content://") -> this
        else -> "$BASE_URL/api/image/${trimStart('/')}"
    }
}

@Composable
private fun rememberPinProvider(
    @DrawableRes resId: Int,
    sizeDp: Float = 32f
): ImageProvider {
    val context = LocalContext.current
    val appContext = remember { context.applicationContext }
    val density = LocalContext.current.resources.displayMetrics.density
    val sizePx = (sizeDp * density).toInt()

    return remember(resId, sizePx) {
        val drawable = AppCompatResources
            .getDrawable(appContext, resId)!!
            .mutate()

        val bmp = createBitmap(sizePx, sizePx)
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, sizePx, sizePx)
        drawable.draw(canvas)

        ImageProvider.fromBitmap(bmp)
    }
}

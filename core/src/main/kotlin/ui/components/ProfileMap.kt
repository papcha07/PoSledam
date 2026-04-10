package ui.components

import android.graphics.Canvas
import android.graphics.PointF
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.createBitmap
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.core.R
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

@Composable
fun ProfileMap(
    modifier: Modifier = Modifier,
    onMapReady: (MapView) -> Unit = {},
    pointClick: (Double, Double) -> Unit,
    myLocation: Point? = null,
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val mapView = remember { MapView(context) }

    var initialCameraSet by remember { mutableStateOf(false) }

    var inputListenerRef by remember { mutableStateOf<InputListener?>(null) }
    var collectionRef by remember { mutableStateOf<MapObjectCollection?>(null) }
    var placemarkRef by remember { mutableStateOf<PlacemarkMapObject?>(null) }

    val pinProvider = rememberPinProvider(R.drawable.ic_lapa_point, sizeDp = 36f)

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
            inputListenerRef?.let { mapView.map.removeInputListener(it) }
            inputListenerRef = null
            collectionRef?.clear()
            collectionRef = null
            placemarkRef = null
            lifecycle.removeObserver(observer)
        }
    }
    Box(modifier.fillMaxWidth()) {

        AndroidView(
            modifier = Modifier.testTag("map"),
            factory = {
                val map = mapView.map

                val myCollection = map.mapObjects.addCollection().also { collectionRef = it }

                val inputListener = object : InputListener {
                    override fun onMapTap(map: Map, point: Point) = Unit

                    override fun onMapLongTap(map: Map, point: Point) {
                        val currentPlacemark = placemarkRef
                        Log.d("point", "${point.longitude}  ${point.latitude}")
                        pointClick(point.longitude, point.latitude)
                        if (currentPlacemark == null) {
                            placemarkRef = myCollection.addPlacemark(point).apply {
                                setIcon(pinProvider)
                                setIconStyle(
                                    IconStyle().apply {
                                        anchor = PointF(0.5f, 1f)
                                        scale = 1f
                                    }
                                )
                            }
                        } else {
                            currentPlacemark.setGeometry(point)
                        }

                        map.move(
                            CameraPosition(point, 16f, 0f, 0f),
                            Animation(
                                Animation.Type.SMOOTH,
                                0.3f
                            ),
                            null
                        )
                    }
                }
                inputListenerRef = inputListener
                map.addInputListener(inputListener)

                mapView
            },
            update = { view ->
                if (!initialCameraSet) {
                    val boundingBox = BoundingBox(
                        Point(56.18, 92.68),
                        Point(55.9, 93.15)
                    )
                    val cp = view.map.cameraPosition(Geometry.fromBoundingBox(boundingBox))
                    view.post {
                        view.map.move(cp)
                        onMapReady(view)
                    }
                    initialCameraSet = true
                }

                val point = myLocation
                if (point != null) {
                    val map = view.map
                    val collection = collectionRef ?: map.mapObjects.addCollection().also {
                        collectionRef = it
                    }

                    val currentPlacemark = placemarkRef
                    if (currentPlacemark == null) {
                        placemarkRef = collection.addPlacemark(point).apply {
                            setIcon(pinProvider)
                            setIconStyle(
                                IconStyle().apply {
                                    anchor = PointF(0.5f, 1f)
                                    scale = 1f
                                }
                            )
                        }
                    } else {
                        currentPlacemark.setGeometry(point)
                    }

                    map.move(
                        CameraPosition(point, 16f, 0f, 0f),
                        Animation(Animation.Type.SMOOTH, 0.3f),
                        null
                    )
                }
            }
        )


        ZoomMenu(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp),
            zoom = {
                mapView.let { mapView ->
                    val map = mapView.map
                    val current = map.cameraPosition
                    val newZoom = current.zoom + 0.5f
                    map.move(
                        CameraPosition(current.target, newZoom, current.azimuth, current.tilt),
                        Animation(Animation.Type.SMOOTH, 0.2f),
                        null
                    )
                }
            },
            unZoom = {
                mapView.let { mapView ->
                    val map = mapView.map
                    val current = map.cameraPosition
                    val newZoom = current.zoom - 0.5f
                    map.move(
                        CameraPosition(current.target, newZoom, current.azimuth, current.tilt),
                        Animation(Animation.Type.SMOOTH, 0.2f),
                        null

                    )
                }
            }
        )
    }

}

@Composable
fun CurrentLocationMap(
    modifier: Modifier = Modifier,
    currentLocation: Point?,
    onLocationResolved: (Double, Double) -> Unit
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val mapView = remember { MapView(context) }

    var collectionRef by remember { mutableStateOf<MapObjectCollection?>(null) }
    var placemarkRef by remember { mutableStateOf<PlacemarkMapObject?>(null) }
    var lastReportedLocation by remember { mutableStateOf<Point?>(null) }

    val pinProvider = rememberPinProvider(R.drawable.ic_lapa_point, sizeDp = 36f)

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
            placemarkRef = null
            lifecycle.removeObserver(observer)
        }
    }

    Box(modifier.fillMaxWidth()) {
        AndroidView(
            modifier = Modifier.testTag("current_location_map"),
            factory = {
                mapView
            },
            update = { view ->
                val point = currentLocation
                if (point != null) {
                    val map = view.map
                    val collection = collectionRef ?: map.mapObjects.addCollection().also {
                        collectionRef = it
                    }

                    val currentPlacemark = placemarkRef
                    if (currentPlacemark == null) {
                        placemarkRef = collection.addPlacemark(point).apply {
                            setIcon(pinProvider)
                            setIconStyle(
                                IconStyle().apply {
                                    anchor = PointF(0.5f, 1f)
                                    scale = 1f
                                }
                            )
                        }
                    } else {
                        currentPlacemark.setGeometry(point)
                    }

                    if (lastReportedLocation == null ||
                        lastReportedLocation?.latitude != point.latitude ||
                        lastReportedLocation?.longitude != point.longitude
                    ) {
                        lastReportedLocation = point
                        onLocationResolved(point.longitude, point.latitude)
                    }

                    map.move(
                        CameraPosition(point, 16f, 0f, 0f),
                        Animation(Animation.Type.SMOOTH, 0.3f),
                        null
                    )
                }
            }
        )

        ZoomMenu(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp),
            zoom = {
                val map = mapView.map
                val current = map.cameraPosition
                val newZoom = current.zoom + 0.5f
                map.move(
                    CameraPosition(current.target, newZoom, current.azimuth, current.tilt),
                    Animation(Animation.Type.SMOOTH, 0.2f),
                    null
                )
            },
            unZoom = {
                val map = mapView.map
                val current = map.cameraPosition
                val newZoom = current.zoom - 0.5f
                map.move(
                    CameraPosition(current.target, newZoom, current.azimuth, current.tilt),
                    Animation(Animation.Type.SMOOTH, 0.2f),
                    null
                )
            }
        )
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


@Composable
fun ZoomMenu(
    modifier: Modifier = Modifier,
    zoom: () -> Unit,
    unZoom: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                color = androidx.compose.ui.graphics.Color.LightGray,
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Image(
            modifier = Modifier.clickable {
                zoom()
            },
            painter = painterResource(R.drawable.ic_plus),
            contentDescription = "Приблизить"
        )
        Spacer(Modifier.height(8.dp))
        Image(
            modifier = Modifier.clickable {
                unZoom()
            },
            painter = painterResource(R.drawable.ic_minus),
            contentDescription = "Отдалить"
        )
    }
}

@Preview
@Composable
private fun ZoomMenuPreview(
    modifier: Modifier = Modifier,
) {
    ZoomMenu(
        zoom = {

        },
        unZoom = {

        }
    )
}
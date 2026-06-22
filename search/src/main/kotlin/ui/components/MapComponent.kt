package ui.components

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.PointF
import android.util.Log
import android.view.MotionEvent
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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

@SuppressLint("ClickableViewAccessibility")
@Composable
fun MapComponent(
    modifier: Modifier = Modifier,
    longitude: Double,
    latitude: Double,
    onTouchStateChanged: (Boolean) -> Unit = {},
    onMapReady: (MapView) -> Unit = {}
) {
    val context = LocalContext.current
    ensureYandexMapKitInitialized(context)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val mapView = remember { MapView(context) }
    val currentOnTouchStateChanged = rememberUpdatedState(onTouchStateChanged)
    var initialCameraSet by remember { mutableStateOf(false) }

    // будем хранить ссылку на коллекцию и на маркер,
    // но юзеру больше не дадим их менять
    var collectionRef by remember { mutableStateOf<MapObjectCollection?>(null) }
    var placemarkRef by remember { mutableStateOf<PlacemarkMapObject?>(null) }

    val pinProvider = rememberPinProvider(R.drawable.ic_lapa_point, sizeDp = 36f)

    // Жизненный цикл MapKit
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
            factory = {
                mapView.setOnTouchListener { _, event ->
                    when (event.actionMasked) {
                        MotionEvent.ACTION_DOWN -> currentOnTouchStateChanged.value(true)
                        MotionEvent.ACTION_UP,
                        MotionEvent.ACTION_CANCEL -> currentOnTouchStateChanged.value(false)
                    }
                    false
                }

                val map = mapView.map

                // создаём коллекцию для плейсмарков и кладём её в state
                val myCollection = map.mapObjects.addCollection().also { collectionRef = it }

                // создаём точку из переданных координат
                val point = Point(latitude, longitude)

                // ставим маркер один раз
                placemarkRef = myCollection.addPlacemark(point).apply {
                    setIcon(pinProvider)
                    setIconStyle(
                        IconStyle().apply {
                            anchor = PointF(0.5f, 1f)
                            scale = 1f
                        }
                    )
                }

                // двигаем камеру к этой точке с зумом
                map.move(
                    CameraPosition(point, 16f, 0f, 0f),
                    Animation(Animation.Type.SMOOTH, 0.3f),
                    null
                )

                // ВАЖНО: не вешаем никакой InputListener,
                // чтобы пользователь не мог добавить новую метку
                // (никаких long tap)

                mapView
            },
            update = { view ->
                // Этот блок вызовется повторно при рекомпозициях.
                // Нам нужно не дублировать перемещения камеры и метки.
                if (!initialCameraSet) {
                    val point = Point(latitude, longitude)

                    // Если по какой-то причине плейсмарка нет (рекомпозиция),
                    // создаём его ещё раз в уже существующей коллекции.
                    if (placemarkRef == null && collectionRef != null) {
                        placemarkRef = collectionRef!!.addPlacemark(point).apply {
                            setIcon(pinProvider)
                            setIconStyle(
                                IconStyle().apply {
                                    anchor = PointF(0.5f, 1f)
                                    scale = 1f
                                }
                            )
                        }
                    }

                    // Наводим камеру только один раз
                    view.map.move(
                        CameraPosition(point, 16f, 0f, 0f),
                        Animation(Animation.Type.SMOOTH, 0.3f),
                        null
                    )

                    onMapReady(view)
                    initialCameraSet = true
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

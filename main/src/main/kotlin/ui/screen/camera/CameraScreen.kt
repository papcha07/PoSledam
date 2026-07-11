package ui.screen.camera

import android.content.Context
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.core.R
import kotlinx.coroutines.launch
import ui.components.default_component.AnimatedToast
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    cameraViewModel: CameraViewModel,
    placeAnimal: () -> Unit,
    catchAnimation: Boolean = false
) {

    val currentContext = LocalContext.current
    val controller = remember {
        LifecycleCameraController(currentContext).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE
            )
        }
    }

    val uris = cameraViewModel.uris.collectAsState()
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val hasUris = remember(uris.value) {
        uris.value.isNotEmpty()
    }
    val showToast = remember {
        mutableStateOf(false)
    }
    val pendingCatchPhoto = remember {
        mutableStateOf<Uri?>(null)
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetSwipeEnabled = pendingCatchPhoto.value == null,
        sheetContent = {
            PhotoBottomSheetContent(
                uris = uris.value,
                modifier = Modifier
                    .fillMaxWidth(),
                onRemovePhoto = cameraViewModel::removePhoto
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {

            CameraPreview(
                controller = controller,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(
                        onClick = {
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            tint = Color.White,
                            contentDescription = "Open gallery"
                        )
                    }

                    IconButton(
                        onClick = {
                            takePhoto(
                                controller = controller,
                                onPhotoTaken = { uri ->
                                    if (catchAnimation) {
                                        pendingCatchPhoto.value = uri
                                    } else {
                                        cameraViewModel.addPhoto(uri)
                                    }
                                },
                                context = currentContext
                            )
                        }
                    ) {
                        Image(
                            painter = painterResource(R.drawable.iphone_button_ic),
                            contentDescription = "Take a photo"
                        )
                    }

                    IconButton(
                        onClick = {
                            if (hasUris) {
                                placeAnimal()
                            } else {
                                showToast.value = true
                            }
                        }
                    ) {
                        Image(
                            modifier = Modifier.size(28.dp),
                            painter = painterResource(R.drawable.right_arrow),
                            contentDescription = "Дальше"
                        )
                    }
                }
            }
            pendingCatchPhoto.value?.let { uri ->
                PokemonCatchOverlay(
                    modifier = Modifier.fillMaxSize(),
                    onFinished = {
                        pendingCatchPhoto.value = null
                        cameraViewModel.addPhoto(uri)
                    }
                )
            }
            if (showToast.value) {
                AnimatedToast(
                    "Сделайте фотографию",
                    onDismiss = {
                        showToast.value = false
                    }
                )
            }
        }
    }


}


private fun takePhoto(
    controller: LifecycleCameraController,
    onPhotoTaken: (Uri) -> Unit,
    context: Context
) {

    val file = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
    val output = ImageCapture.OutputFileOptions.Builder(file).build()


    controller.takePicture(
        output,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val uri = Uri.fromFile(file)
                onPhotoTaken(uri)
            }

            override fun onError(exc: ImageCaptureException) {
                exc.printStackTrace()
            }
        }
    )
}

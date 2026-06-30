package ui.components.photo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.core.content.ContextCompat
import com.example.core.R
import domain.models.REPORT_PHOTO_LIMIT
import java.io.File

data class PhotoAttachmentPickerState(
    val canAddPhotos: Boolean,
    val openPicker: () -> Unit
)

@Composable
fun rememberPhotoAttachmentPickerState(
    selectedPhotoCount: Int,
    onPhotosSelected: (List<Uri>) -> Unit,
    maxPhotos: Int = REPORT_PHOTO_LIMIT
): PhotoAttachmentPickerState {
    val context = LocalContext.current
    val availableSlots = (maxPhotos - selectedPhotoCount).coerceAtLeast(0)
    var showSourceDialog by remember { mutableStateOf(false) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var launchCameraAfterPermission by remember { mutableStateOf(false) }

    val singleGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null && availableSlots > 0) {
            onPhotosSelected(listOf(uri))
        }
    }

    val multipleGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(
            maxItems = availableSlots.coerceAtLeast(2)
        )
    ) { uris ->
        val selectedUris = uris.take(availableSlots)
        if (selectedUris.isNotEmpty()) {
            onPhotosSelected(selectedUris)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { isSaved ->
        val uri = pendingCameraUri
        pendingCameraUri = null
        if (isSaved && uri != null && availableSlots > 0) {
            onPhotosSelected(listOf(uri))
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && launchCameraAfterPermission) {
            launchCameraAfterPermission = false
            val uri = context.createCameraImageUri()
            pendingCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            launchCameraAfterPermission = false
        }
    }

    fun launchGallery() {
        showSourceDialog = false
        val request = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        if (availableSlots == 1) {
            singleGalleryLauncher.launch(request)
        } else if (availableSlots > 1) {
            multipleGalleryLauncher.launch(request)
        }
    }

    fun launchCamera() {
        showSourceDialog = false
        if (availableSlots <= 0) return
        if (context.shouldRequestCameraPermission()) {
            launchCameraAfterPermission = true
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            return
        }
        val uri = context.createCameraImageUri()
        pendingCameraUri = uri
        cameraLauncher.launch(uri)
    }

    if (showSourceDialog) {
        PhotoSourceDialog(
            availableSlots = availableSlots,
            onDismiss = { showSourceDialog = false },
            onGalleryClick = ::launchGallery,
            onCameraClick = ::launchCamera
        )
    }

    return PhotoAttachmentPickerState(
        canAddPhotos = availableSlots > 0,
        openPicker = {
            if (availableSlots > 0) {
                showSourceDialog = true
            }
        }
    )
}

@Composable
private fun PhotoSourceDialog(
    availableSlots: Int,
    onDismiss: () -> Unit,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFFFAFAFA),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Добавить фото",
                    style = TextStyle(
                        fontFamily = LebowskiByPragmatica,
                        fontSize = 26.sp,
                        lineHeight = 30.sp,
                        letterSpacing = (-0.26).sp,
                        color = Color(0xFF1E1E1E),
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Можно добавить еще $availableSlots",
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    color = Color(0xFF777777),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(18.dp))

                PhotoSourceOption(
                    icon = R.drawable.ic_uploadpet,
                    title = "Выбрать из галереи",
                    subtitle = "Добавить готовое фото",
                    onClick = onGalleryClick
                )

                Spacer(Modifier.height(10.dp))

                PhotoSourceOption(
                    icon = R.drawable.ic_camera_add,
                    title = "Сделать фото",
                    subtitle = "Открыть камеру",
                    onClick = onCameraClick
                )

                Spacer(Modifier.height(10.dp))

                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Отмена",
                        color = Color(0xFF777777),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoSourceOption(
    icon: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = Color(0xFFE5E5E5),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(34.dp),
            painter = painterResource(icon),
            contentDescription = null
        )

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 17.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF222222)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                lineHeight = 17.sp,
                color = Color(0xFF8E8E93)
            )
        }
    }
}

private fun Context.createCameraImageUri(): Uri {
    val imageDirectory = File(cacheDir, CAMERA_IMAGE_DIR).apply {
        mkdirs()
    }
    val imageFile = File.createTempFile(CAMERA_IMAGE_PREFIX, CAMERA_IMAGE_SUFFIX, imageDirectory)
    return FileProvider.getUriForFile(
        this,
        "$packageName.fileprovider",
        imageFile
    )
}

private fun Context.shouldRequestCameraPermission(): Boolean {
    return isPermissionDeclared(Manifest.permission.CAMERA) &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
}

private fun Context.isPermissionDeclared(permission: String): Boolean {
    return runCatching {
        packageManager
            .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            .requestedPermissions
            ?.contains(permission) == true
    }.getOrDefault(false)
}

private const val CAMERA_IMAGE_DIR = "camera_images"
private const val CAMERA_IMAGE_PREFIX = "report_photo_"
private const val CAMERA_IMAGE_SUFFIX = ".jpg"

private val LebowskiByPragmatica = FontFamily(
    Font(R.font.lebowski_by_pragmatica_regular)
)

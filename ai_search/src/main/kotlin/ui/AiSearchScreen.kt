package ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.core.R
import domain.models.SimilarAnnouncement
import ui.components.AssistantMessage
import ui.components.AssistantResultMessage
import ui.components.ChatDateLabel
import ui.components.HistoryChatMessage
import ui.components.TypingMessage
import ui.components.UserPhotoMessage
import ui.components.default_component.ToolBar
import ui.components.default_component.ToolBarInfo
import ui.models.ChatRole
import ui.theme.buttonPrimary
import ui.viewModel.AiSearchViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val ScreenBackground = Color(0xFFF6F6F8)
private val DisabledButtonColor = Color(0xFFC9C9D2)

@Composable
fun AiSearchScreen(
    viewModel: AiSearchViewModel,
    onOpenAnnouncement: (id: String, type: Int) -> Unit
) {
    val context = LocalContext.current
    val selectedPhoto by viewModel.selectedPhoto.collectAsState()
    val isSending by viewModel.isSending.collectAsState()
    val waitingResult by viewModel.waitingResult.collectAsState()
    val conversation by viewModel.conversation.collectAsState()
    val history = viewModel.history.collectAsLazyPagingItems()
    val listState = rememberLazyListState()

    val dateLabel = remember {
        "Сегодня " + SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { viewModel.onPhotoSelected(it) } }
    fun pickPhoto() = pickImageLauncher.launch("image/*")

    val openSimilar: (SimilarAnnouncement) -> Unit = { item ->
        when (item.type) {
            0, 1, 2 -> onOpenAnnouncement(item.id, item.type)
            else -> Toast.makeText(
                context,
                "Не удалось открыть объявление. Неизвестный тип результата.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(conversation.size, selectedPhoto) {
        val total = listState.layoutInfo.totalItemsCount
        if (total > 0) listState.animateScrollToItem(total - 1)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
    ) {
        ToolBar(toolBarInfo = ToolBarInfo(title = "Умный поиск"))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = listState,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = 16.dp,
                vertical = 8.dp
            )
        ) {
            item("date") { ChatDateLabel(text = dateLabel) }

            items(viewModel.greeting, key = { it.id }) { message ->
                AssistantMessage(text = message.text)
            }

            when (val refresh = history.loadState.refresh) {
                is LoadState.Loading -> item("history_loading") {
                    TypingMessage(text = "Загружаю историю…")
                }

                is LoadState.Error -> item("history_error") {
                    AssistantMessage(text = "Не удалось загрузить историю поисков.", warning = true)
                }

                is LoadState.NotLoading -> if (history.itemCount > 0) {
                    item("history_header") { AssistantMessage(text = "Ваши последние поиски") }
                    items(
                        count = history.itemCount,
                        key = { index -> history.peek(index)?.createdAt ?: index }
                    ) { index ->
                        history[index]?.let { entry ->
                            HistoryChatMessage(entry = entry, onOpen = openSimilar)
                        }
                    }
                    if (history.loadState.append is LoadState.Loading) {
                        item("history_append") { TypingMessage(text = "Загружаю ещё…") }
                    }
                }
            }

            items(conversation, key = { it.id }) { message ->
                when (message.role) {
                    ChatRole.User -> UserPhotoMessage(imageModel = message.imageUri)
                    ChatRole.Loading -> TypingMessage(text = message.text ?: "…")
                    ChatRole.System ->
                        AssistantMessage(text = message.text, warning = true)

                    ChatRole.Assistant ->
                        if (message.results.isNotEmpty()) {
                            AssistantResultMessage(
                                text = message.text.orEmpty(),
                                results = message.results,
                                onOpen = openSimilar
                            )
                        } else {
                            AssistantMessage(text = message.text)
                        }
                }
            }

            // Превью ещё не подтверждённого фото — как сообщение пользователя.
            selectedPhoto?.let { uri ->
                item("pending_photo") { UserPhotoMessage(imageModel = uri) }
            }
        }

        BottomActionPanel(
            hasPhoto = selectedPhoto != null,
            isSending = isSending,
            waitingResult = waitingResult,
            onPick = ::pickPhoto,
            onConfirm = viewModel::confirm
        )
    }
}

@Composable
private fun BottomActionPanel(
    hasPhoto: Boolean,
    isSending: Boolean,
    waitingResult: Boolean,
    onPick: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp), clip = false, spotColor = Color(0x1F000000))
            .background(Color.White, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        when {
            isSending -> DisabledPrimaryButton(text = "Ищем…")

            hasPhoto -> Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AccentOutlineButton(
                    text = "Выбрать другое",
                    onClick = onPick,
                    modifier = Modifier.weight(1f)
                )
                AccentButton(
                    text = "Подтвердить",
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f)
                )
            }

            waitingResult -> {
                Text(
                    text = "Результат придёт в уведомлении",
                    fontSize = 14.sp,
                    color = Color(0xFF787878),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )
                AccentOutlineButton(
                    text = "Выбрать другое фото",
                    onClick = onPick,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            else -> AccentButton(
                text = "Выбрать фото",
                onClick = onPick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AccentButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = buttonPrimary, contentColor = Color.White)
    ) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun AccentOutlineButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(54.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, buttonPrimary),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = buttonPrimary)
    ) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DisabledPrimaryButton(text: String, modifier: Modifier = Modifier) {
    Button(
        onClick = {},
        enabled = false,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = DisabledButtonColor,
            disabledContentColor = Color.White
        )
    ) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

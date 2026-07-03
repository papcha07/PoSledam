package ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.core.R
import domain.models.AiSearchResult
import domain.models.SimilarAnnouncement
import ui.components.AssistantMessage
import ui.components.AssistantResultMessage
import ui.components.ChatDateLabel
import ui.components.TypingMessage
import ui.components.UserPhotoMessage
import ui.components.default_component.ToolBar
import ui.components.default_component.ToolBarInfo
import ui.models.AiSearchResultUiState
import ui.viewModel.AiSearchResultViewModel

private val ScreenBg = Color(0xFFF6F6F8)

@Composable
fun AiSearchResultScreen(
    requestId: String,
    viewModel: AiSearchResultViewModel,
    onBackClick: () -> Unit,
    onOpenAnnouncement: (id: String, type: Int) -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(requestId) { viewModel.load(requestId) }
    val state by viewModel.state.collectAsState()

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {
        ToolBar(
            toolBarInfo = ToolBarInfo(
                title = "Умный поиск",
                backArrow = true,
                backArrowIcon = R.drawable.left_arrow,
                onBackClick = onBackClick
            )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = 16.dp,
                vertical = 8.dp
            )
        ) {
            when (val current = state) {
                is AiSearchResultUiState.Loading -> item("loading") {
                    TypingMessage(text = "Ищу похожие объявления…")
                }

                is AiSearchResultUiState.Error -> item("error") {
                    AssistantMessage(text = current.message, warning = true)
                }

                is AiSearchResultUiState.Success -> resultMessages(
                    result = current.result,
                    onOpen = openSimilar
                )
            }
        }
    }
}

private fun LazyListScope.resultMessages(
    result: AiSearchResult,
    onOpen: (SimilarAnnouncement) -> Unit
) {
    result.searchImagePath?.let { path ->
        item("photo") {
            UserPhotoMessage(imageModel = ui.components.aiImageModel(path))
        }
    }

    when {
        result.hasError -> item("err") {
            AssistantMessage(text = errorMessage(result.errorCode), warning = true)
        }

        result.isEmpty -> item("empty") {
            AssistantMessage(text = "По этому фото я не нашёл похожих объявлений.")
        }

        else -> item("result") {
            AssistantResultMessage(
                text = "Я нашёл похожие объявления:",
                results = result.results,
                onOpen = onOpen
            )
        }
    }
}

private fun errorMessage(errorCode: String?): String = when (errorCode) {
    "ANIMAL_NOT_FOUND" ->
        "На фото не удалось распознать животное. Попробуйте выбрать другое фото."

    "MULTIPLE_ANIMALS_FOUND" ->
        "На фото найдено несколько животных. Загрузите фото, где хорошо видно одного питомца."

    else -> "Не удалось выполнить поиск по этому фото. Попробуйте ещё раз."
}

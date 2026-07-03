package ui.models

import android.net.Uri
import domain.models.SimilarAnnouncement
import java.util.UUID

enum class ChatRole { Assistant, User, System, Loading }

/**
 * Одно сообщение в диалоге с нейро-помощником.
 *
 * - [ChatRole.Assistant] — реплика помощника слева (текст и/или карточки результата).
 * - [ChatRole.User] — реплика пользователя справа (обычно отправленное фото).
 * - [ChatRole.System] — спокойное информационное сообщение (ошибки, статусы).
 * - [ChatRole.Loading] — «печатает…» пузырь помощника.
 */
data class AiChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: ChatRole,
    val text: String? = null,
    /** Локальное фото, отправленное пользователем. */
    val imageUri: Uri? = null,
    /** Похожие объявления в ответе помощника. */
    val results: List<SimilarAnnouncement> = emptyList()
)

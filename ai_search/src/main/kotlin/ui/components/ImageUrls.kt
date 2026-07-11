package ui.components

import ui.BASE_URL

/**
 * Приводит путь к картинке к корректной модели для Coil.
 *
 * Backend в нейропоиске отдаёт searchImagePath/mainImagePath уже как абсолютный URL
 * (например https://storage.yandexcloud.net/...). Такой путь нельзя префиксовать
 * baseUrl/api/image — используем как есть. Относительные пути (как в остальном
 * приложении) по-прежнему собираем через $BASE_URL/api/image/.
 *
 * Загруженные картинки Coil кэширует на устройстве (память + диск) автоматически.
 */
internal fun aiImageModel(path: String?): String? {
    val value = path?.takeIf { it.isNotBlank() } ?: return null
    return if (value.startsWith("http", ignoreCase = true)) {
        value
    } else {
        "$BASE_URL/api/image/$value"
    }
}

package ui.model

import NewsType
import androidx.annotation.DrawableRes

data class ArticleInfo(
    val newsType: NewsType,
    @DrawableRes
    val image: Int
)

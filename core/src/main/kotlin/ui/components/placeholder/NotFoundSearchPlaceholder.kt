package ui.components.placeholder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.R

@Composable
fun NotFoundSearchPlaceholder(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .width(280.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Следов пока нет",
            style = TextStyle(
                fontFamily = LebowskiByPragmatica,
                fontWeight = FontWeight.Normal,
                fontSize = 26.sp,
                lineHeight = 29.38.sp,
                letterSpacing = (-0.26).sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Но новые объявления появляются\nрегулярно. Проверьте позже или\nизмените параметры поиска",
            style = TextStyle(
                fontFamily = LebowskiByPragmatica,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                color = Color(0xFF5E5E5E),
                textAlign = TextAlign.Center
            )
        )
    }
}

private val LebowskiByPragmatica = FontFamily(
    Font(R.font.lebowski_by_pragmatica_regular)
)

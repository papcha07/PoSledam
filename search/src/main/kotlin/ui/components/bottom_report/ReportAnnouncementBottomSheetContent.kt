package ui.components.bottom_report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.R
import ui.theme.Ser
import ui.theme.buttonPrimary

@Composable
fun ReportAnnouncementBottomSheetContent(
    modifier: Modifier = Modifier,
    comment: String,
    isLoading: Boolean,
    commentLimit: Int,
    onCommentChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    val trimmedComment = comment.trim()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Пожаловаться",
            textAlign = TextAlign.Center,
            fontSize = 26.sp,
            color = Color(0xFF1E1E1E)
        )

        Spacer(Modifier.height(22.dp))

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 144.dp)
                .clip(RoundedCornerShape(14.dp)),
            value = comment,
            onValueChange = { value ->
                onCommentChange(value.take(commentLimit))
            },
            enabled = !isLoading,
            placeholder = {
                Text(
                    text = "Опишите причину жалобы",
                    color = Color(0xFF9B9B9B),
                    fontSize = 15.sp
                )
            },
            textStyle = TextStyle(
                color = Color(0xFF1E1E1E),
                fontSize = 15.sp,
                lineHeight = 21.sp
            ),
            minLines = 5,
            maxLines = 7,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE7E7E7),
                unfocusedContainerColor = Color(0xFFE7E7E7),
                disabledContainerColor = Color(0xFFE7E7E7),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = buttonPrimary
            )
        )

        Spacer(Modifier.height(8.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "${comment.length}/$commentLimit",
            textAlign = TextAlign.End,
            fontSize = 12.sp,
            color = Ser
        )

        Spacer(Modifier.height(22.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = trimmedComment.isNotEmpty() && !isLoading,
            shape = RoundedCornerShape(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonPrimary,
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFE0E0E0),
                disabledContentColor = Color.White
            ),
            onClick = onSendClick
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.height(22.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Отправить жалобу",
                    fontSize = 16.sp
                )
            }
        }

        Spacer(Modifier.height(12.dp))
    }
}

private val LebowskiByPragmatica = FontFamily(
    Font(R.font.lebowski_by_pragmatica_regular)
)

package ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.R
import ui.theme.purpleStatusColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoveReasonBottomSheet(
    selectedReason: Int,
    onReasonSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
    onDelete: (Int) -> Unit
) {
    val reasons = listOf(
        "Хозяин найден" to 0,
        "Хозяин не найден, я оставил(а) себе" to 1,
        "Хозяин не найден, отдал(а) в другие руки" to 2,
        "На объявление долго никто не откликается" to 3
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        scrimColor = Color.White, // это полупрозрачный фон за BottomSheet
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Surface(
            color = Color.White, // <-- белый фон всей шторки
            modifier = Modifier.fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Укажите причину снятия публикации",
                    fontSize = 18.sp
                )

                Spacer(Modifier.height(24.dp))

                reasons.forEach { (text, id) ->
                    ReasonItem(
                        text = text,
                        selected = id == selectedReason,
                        onClick = { onReasonSelected(id) }
                    )
                    Spacer(Modifier.height(8.dp))
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { onDelete(selectedReason) },
                    enabled = selectedReason >= 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        "Удалить объявление",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }

}

@Composable
fun ReasonItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected)
            purpleStatusColor.copy(alpha = 0.08f)
        else
            MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = if (selected) painterResource(R.drawable.check) else painterResource(R.drawable.uncheck),
                contentDescription = "checked"
            )
        }
    }
}





package ui.components.bottom_reason

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomReasonComponent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                color = Color.White,
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp
                )
            )
            .fillMaxWidth()
    ) {
        Column {
            CheckboxRow(
                text = "Хозяйн найден",
            )
            CheckboxRow(
                text = "Хозяин не найден, я оставл(а) себе",
            )
            CheckboxRow(
                text = "Хозяин не найден, отдал(а) в другие руки",
            )
            CheckboxRow(
                text = "На объявление долго никто не откликается",
            )
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun CheckboxRow(
    text: String,
) {
    var checked by remember { mutableStateOf(true) }

    val changeModifier = Modifier
        .padding(horizontal = 12.dp, vertical = 14.dp)
        .background(
            color = Color.White,
            shape = RoundedCornerShape(10.dp)
        )
    Row(
        modifier = changeModifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            color = Color.Black,
            fontSize = 14.sp
        )
        Checkbox(
            checked = checked,
            onCheckedChange = { checked = it }
        )
    }
}

@Preview
@Composable
private fun CheckBoxRowPreview() {
    CheckboxRow("asd")
}

@Preview
@Composable
private fun BottomReasonComponentPreview() {
    BottomReasonComponent()
}
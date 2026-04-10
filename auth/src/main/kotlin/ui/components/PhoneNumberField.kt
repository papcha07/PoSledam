package ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.R
import ui.theme.EditTextColor

@Composable
fun PhoneNumberField(
    modifier: Modifier = Modifier,
    phone: String,
    onPhoneChange: (String) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Column(
            Modifier
                .background(
                    color = Color(0xFFF8F8F8),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(10.dp)
                .fillMaxHeight()
        ) {
            Text(
                text = "Код",
                fontSize = 10.sp,
                color = Color(0xFF686868)
            )
            Row {
                Image(
                    painter = painterResource(R.drawable.ic_russia_flag),
                    contentDescription = null
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "+7",
                    fontSize = 14.sp
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        PhoneTextFieldComponent(
            value = phone,
            onValueChange = onPhoneChange
        )
    }
}

@Composable
fun PhoneTextFieldComponent(
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp)),
        value = value,
        onValueChange = { onValueChange(it) },
        label = { Text(text = "Номер телефона") },
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = EditTextColor,
            unfocusedContainerColor = EditTextColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedTextColor = Color.Black,
            focusedPlaceholderColor = Color.Black
        )
    )
}

@Preview
@Composable
private fun PhoneNumberFieldPreview() {
    var phone by remember { mutableStateOf("") }
    PhoneNumberField(
        modifier = Modifier,
        phone = phone,
        onPhoneChange = { phone = it }
    )
}
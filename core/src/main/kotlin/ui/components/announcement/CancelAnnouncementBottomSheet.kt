package ui.components.announcement

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.R
import ui.model.AnnouncementCancelReason
import ui.model.AnnouncementCancelReasonOption
import ui.theme.deleteButtonColor
import ui.theme.purpleStatusColor

@Composable
fun CancelAnnouncementReasonContent(
    selectedReasonId: Int,
    reasons: List<AnnouncementCancelReasonOption>,
    onReasonSelected: (Int) -> Unit,
    onCancelAnnouncement: (Int) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Укажите причину снятия публикации",
    actionText: String = "Удалить объявление"
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(top = 32.dp),
            text = title,
            textAlign = TextAlign.Center,
            fontSize = 22.sp,
            color = Color(0xFF1E1E1E)
        )

        Spacer(Modifier.height(32.dp))

        reasons.forEach { reason ->
            CancelAnnouncementReasonItem(
                text = reason.title,
                selected = reason.id == selectedReasonId,
                onClick = { onReasonSelected(reason.id) }
            )
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(30.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            onClick = { onCancelAnnouncement(selectedReasonId) },
            enabled = reasons.any { it.id == selectedReasonId },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEFDBDB),
                contentColor = Color(0xFFFF3B3B),
                disabledContainerColor = Color(0xFFEFDBDB).copy(alpha = 0.5f),
                disabledContentColor = Color(0xFFFF3B3B).copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(14.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                disabledElevation = 0.dp
            )
        ) {
            Text(
                text = actionText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun CancelAnnouncementReasonItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .background(
                color = if (selected) Color(0xFFF7EDED) else Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(shape = RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = text,
            fontSize = 16.sp,
            color = Color(0xFF1E1E1E)
        )
        Image(
            painter = painterResource(if (selected) R.drawable.check else R.drawable.uncheck),
            contentDescription = null
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CancelAnnouncementReasonContentPreview() {
    MaterialTheme {
        CancelAnnouncementReasonContent(
            selectedReasonId = AnnouncementCancelReason.OwnerFound.id,
            reasons = AnnouncementCancelReason.defaultOptions,
            onReasonSelected = {},
            onCancelAnnouncement = {}
        )
    }
}

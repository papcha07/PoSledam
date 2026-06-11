package ui.components.placeholder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PetCardShimmerPlaceholder(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(15.dp),
                clip = false,
                spotColor = Color.Black.copy(alpha = 0.35f),
                ambientColor = Color.Black.copy(alpha = 0.35f)
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(15.dp)
            )
            .clip(RoundedCornerShape(15.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            Box {
                ShimmerImagePlaceholder(
                    modifier = Modifier
                        .size(width = 130.dp, height = 140.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                ShimmerImagePlaceholder(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(Modifier.width(14.dp))

            Box(
                modifier = Modifier
                    .height(140.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    ShimmerTextPlaceholder(
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth(0.45f)
                    )
                    Spacer(Modifier.height(8.dp))
                    ShimmerTextPlaceholder(
                        modifier = Modifier
                            .height(12.dp)
                            .fillMaxWidth(0.95f)
                    )
                    Spacer(Modifier.height(6.dp))
                    ShimmerTextPlaceholder(
                        modifier = Modifier
                            .height(12.dp)
                            .fillMaxWidth(0.88f)
                    )
                    Spacer(Modifier.height(6.dp))
                    ShimmerTextPlaceholder(
                        modifier = Modifier
                            .height(12.dp)
                            .fillMaxWidth(0.72f)
                    )
                }

                ShimmerTextPlaceholder(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .height(16.dp)
                        .fillMaxWidth(0.55f)
                )
            }
        }
    }
}

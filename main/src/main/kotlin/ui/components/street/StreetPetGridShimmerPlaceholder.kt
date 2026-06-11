package ui.components.street

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ui.components.placeholder.ShimmerImagePlaceholder
import ui.components.placeholder.ShimmerTextPlaceholder

@Composable
fun StreetPetGridShimmerPlaceholder(
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 16.dp,
            end = 16.dp,
            bottom = 16.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            NearStreetPetCardShimmerPlaceholder()
        }

        items(STREET_PET_SMALL_SHIMMER_COUNT) {
            StreetPetCardShimmerPlaceholder()
        }
    }
}

@Composable
fun StreetPetAppendShimmerPlaceholder(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StreetPetCardShimmerPlaceholder(modifier = Modifier.weight(1f))
        StreetPetCardShimmerPlaceholder(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun NearStreetPetCardShimmerPlaceholder(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(332.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            ShimmerImagePlaceholder(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 20.dp)
                    .width(250.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ShimmerTextPlaceholder(
                        modifier = Modifier
                            .height(22.dp)
                            .weight(1f)
                    )
                    ShimmerTextPlaceholder(
                        modifier = Modifier
                            .height(22.dp)
                            .weight(0.7f)
                    )
                }
                Spacer(Modifier.height(8.dp))
                ShimmerTextPlaceholder(
                    modifier = Modifier
                        .height(22.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun StreetPetCardShimmerPlaceholder(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        ShimmerImagePlaceholder(
            modifier = Modifier
                .size(170.dp)
                .clip(RoundedCornerShape(13.dp))
        )
        Spacer(Modifier.height(14.dp))
        ShimmerTextPlaceholder(
            modifier = Modifier
                .height(14.dp)
                .fillMaxWidth(0.64f)
        )
        Spacer(Modifier.height(4.dp))
        ShimmerTextPlaceholder(
            modifier = Modifier
                .height(14.dp)
                .fillMaxWidth(0.9f)
        )
        Spacer(Modifier.height(8.dp))
        Row {
            ShimmerTextPlaceholder(
                modifier = Modifier
                    .height(18.dp)
                    .width(78.dp)
            )
            Spacer(Modifier.width(5.dp))
            ShimmerTextPlaceholder(
                modifier = Modifier
                    .height(18.dp)
                    .width(46.dp)
            )
        }
    }
}

private const val STREET_PET_SMALL_SHIMMER_COUNT = 6

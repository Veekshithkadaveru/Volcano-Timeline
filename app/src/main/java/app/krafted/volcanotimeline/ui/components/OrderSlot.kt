package app.krafted.volcanotimeline.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OrderSlot(
    index: Int,
    totalItems: Int,
    modifier: Modifier = Modifier
) {
    val positionLabel = when (index) {
        0 -> "OLDEST"
        totalItems - 1 -> "NEWEST"
        1 -> "2ND"
        2 -> "3RD"
        3 -> "4TH"
        else -> "${index + 1}TH"
    }

    val infiniteTransition = rememberInfiniteTransition(label = "slot_$index")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            tween(1800 + index * 200, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "slotBorder_$index"
    )
    val shimmerX by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            tween(2400 + index * 300, easing = FastOutSlowInEasing),
            RepeatMode.Restart
        ),
        label = "shimmer_$index"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF0D0D1A).copy(alpha = 0.7f),
                            Color(0xFF0A0A14).copy(alpha = 0.5f)
                        )
                    )
                )
                .border(
                    1.5.dp,
                    Color(0xFFE65100).copy(alpha = borderAlpha),
                    RoundedCornerShape(12.dp)
                )
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val shimmerFraction = shimmerX.coerceIn(0f, 1f)
                val shimmerCenter = size.width * shimmerFraction
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFFE65100).copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        startX = shimmerCenter - 80f,
                        endX = shimmerCenter + 80f
                    ),
                    size = size
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE65100).copy(alpha = 0.18f))
                        .border(
                            1.dp,
                            Color(0xFFE65100).copy(alpha = borderAlpha + 0.15f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${index + 1}",
                        color = Color(0xFFE65100).copy(alpha = 0.9f),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = positionLabel,
                        color = Color.White.copy(alpha = 0.28f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 2.sp
                    )
                    if (index == 0 || index == 3) {
                        Text(
                            text = if (index == 0) "↑ place here" else "place here ↓",
                            color = Color(0xFFE65100).copy(alpha = 0.22f),
                            fontSize = 9.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}

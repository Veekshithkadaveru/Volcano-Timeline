package app.krafted.volcanotimeline.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.volcanotimeline.data.model.Eruption

@Composable
fun EruptionCard(
    eruption: Eruption,
    isConfirmed: Boolean,
    isCorrect: Boolean,
    isDragging: Boolean = false,
    modifier: Modifier = Modifier
) {
    val borderColor = when {
        isDragging -> Color(0xFFFF6D00)
        isConfirmed && isCorrect -> Color(0xFF4CAF50)
        isConfirmed && !isCorrect -> Color(0xFFEF5350)
        else -> Color(0xFFE65100).copy(alpha = 0.45f)
    }
    val borderWidth: Dp = if (isDragging) 2.dp else 1.5.dp

    val backgroundBrush = when {
        isDragging -> Brush.horizontalGradient(
            listOf(Color(0xFF2A1A0E), Color(0xFF1A1A2E))
        )

        isConfirmed && isCorrect -> Brush.horizontalGradient(
            listOf(Color(0xFF1B5E20).copy(alpha = 0.55f), Color(0xFF1A1A2E))
        )

        isConfirmed && !isCorrect -> Brush.horizontalGradient(
            listOf(Color(0xFFB71C1C).copy(alpha = 0.45f), Color(0xFF1A1A2E))
        )

        else -> Brush.horizontalGradient(
            listOf(Color(0xFF1E1E30), Color(0xFF16213E))
        )
    }

    var showReveal by remember(isConfirmed) { mutableStateOf(false) }
    LaunchedEffect(isConfirmed) {
        if (isConfirmed) showReveal = true
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = if (isDragging) 1.04f else 1f
                scaleY = if (isDragging) 1.04f else 1f
                shadowElevation = if (isDragging) 28f else 4f
            }
            .clip(RoundedCornerShape(14.dp))
            .border(borderWidth, borderColor, RoundedCornerShape(14.dp))
            .background(backgroundBrush)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!isConfirmed) {
                DragHandle(
                    color = if (isDragging) Color(0xFFFF6D00) else Color(0xFFE65100).copy(alpha = 0.55f),
                    modifier = Modifier.padding(end = 12.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = eruption.volcano,
                    color = if (isDragging) Color.White else Color.White.copy(alpha = 0.95f),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp,
                    textAlign = TextAlign.Start,
                    lineHeight = 22.sp
                )
                Text(
                    text = eruption.country,
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Start,
                    letterSpacing = 0.3.sp
                )
                if (!isConfirmed) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = if (isDragging) "releasing will place here" else "hold & drag to reorder",
                        color = if (isDragging) Color(0xFFFF6D00).copy(alpha = 0.7f)
                        else Color.White.copy(alpha = 0.22f),
                        fontSize = 10.sp,
                        letterSpacing = 0.4.sp,
                        fontWeight = if (isDragging) FontWeight.Medium else FontWeight.Normal
                    )
                }
            }

            AnimatedVisibility(
                visible = isConfirmed && showReveal,
                enter = fadeIn(tween(300)) + scaleIn(tween(300, easing = { t ->
                    val s = 1.70158f
                    val t2 = t - 1f
                    t2 * t2 * ((s + 1) * t2 + s) + 1f
                }))
            ) {
                Spacer(modifier = Modifier.width(12.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(
                                if (isCorrect) Brush.radialGradient(
                                    listOf(
                                        Color(0xFF66BB6A),
                                        Color(0xFF2E7D32)
                                    )
                                )
                                else Brush.radialGradient(
                                    listOf(
                                        Color(0xFFEF5350),
                                        Color(0xFFC62828)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isCorrect) "✓" else "✗",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = eruption.displayYear,
                        color = if (isCorrect) Color(0xFFFFD54F) else Color(0xFFEF9A9A),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DragHandle(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(width = 14.dp, height = 20.dp)) {
        val dotR = 2.2f * density
        val colGap = 7f * density
        val rowGap = 6.5f * density
        val startX = size.width / 2f - colGap / 2f
        val startY = size.height / 2f - rowGap
        for (col in 0..1) {
            for (row in 0..2) {
                drawCircle(
                    color = color,
                    radius = dotR,
                    center = androidx.compose.ui.geometry.Offset(
                        startX + col * colGap,
                        startY + row * rowGap
                    )
                )
            }
        }
    }
}

package app.krafted.volcanotimeline.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.volcanotimeline.ui.theme.CraterGreen
import app.krafted.volcanotimeline.ui.theme.LavaOrange
import app.krafted.volcanotimeline.ui.theme.LavaOrangeBright

enum class VolcanoButtonStyle { Primary, Success, Ghost }

@Composable
fun VolcanoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: VolcanoButtonStyle = VolcanoButtonStyle.Primary,
    height: Dp = if (style == VolcanoButtonStyle.Ghost) 52.dp else 62.dp,
    fontSize: androidx.compose.ui.unit.TextUnit = if (style == VolcanoButtonStyle.Ghost) 15.sp else 18.sp,
    letterSpacing: androidx.compose.ui.unit.TextUnit = if (style == VolcanoButtonStyle.Ghost) 2.sp else 1.5.sp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh),
        label = "btnScale"
    )

    val backgroundModifier = when (style) {
        VolcanoButtonStyle.Primary -> Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(listOf(LavaOrange, LavaOrangeBright))
            )

        VolcanoButtonStyle.Success -> Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(listOf(CraterGreen.copy(red = 0.18f), CraterGreen))
            )

        VolcanoButtonStyle.Ghost -> Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.07f))
            .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(18.dp))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .then(backgroundModifier)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (style == VolcanoButtonStyle.Ghost) Color.White.copy(alpha = 0.85f) else Color.White,
            fontSize = fontSize,
            fontWeight = if (style == VolcanoButtonStyle.Ghost) FontWeight.SemiBold else FontWeight.ExtraBold,
            letterSpacing = letterSpacing
        )
    }
}

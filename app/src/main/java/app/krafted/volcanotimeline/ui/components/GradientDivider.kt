package app.krafted.volcanotimeline.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.krafted.volcanotimeline.ui.theme.LavaOrange

@Composable
fun GradientDivider(
    modifier: Modifier = Modifier,
    color: Color = LavaOrange,
    thickness: Dp = 1.5.dp,
    colorAlpha: Float = 0.6f
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        color.copy(alpha = colorAlpha),
                        Color.Transparent
                    )
                )
            )
    )
}

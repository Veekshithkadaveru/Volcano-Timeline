package app.krafted.volcanotimeline.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.volcanotimeline.ui.util.difficultyColor

@Composable
fun DifficultyBadge(
    difficulty: String,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 8.dp,
    backgroundAlpha: Float = 0.14f,
    borderAlpha: Float = 0.3f,
    textAlpha: Float = 1f
) {
    val color = difficultyColor(difficulty)
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .clip(shape)
            .background(color.copy(alpha = backgroundAlpha))
            .border(0.5.dp, color.copy(alpha = borderAlpha), shape)
            .padding(horizontal = 9.dp, vertical = 3.dp)
    ) {
        Text(
            text = difficulty.uppercase(),
            color = color.copy(alpha = textAlpha),
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.5.sp
        )
    }
}

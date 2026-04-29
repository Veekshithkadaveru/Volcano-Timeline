package app.krafted.volcanotimeline.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import app.krafted.volcanotimeline.ui.theme.VolcanoDark
import app.krafted.volcanotimeline.ui.theme.VolcanoMidnight

@Composable
fun ScreenBackground(
    backgroundResId: Int?,
    modifier: Modifier = Modifier,
    overlayAlphas: Triple<Float, Float, Float> = Triple(0.82f, 0.80f, 0.87f),
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (backgroundResId != null) {
            Image(
                painter = painterResource(id = backgroundResId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (backgroundResId != null) {
                        Brush.verticalGradient(
                            listOf(
                                VolcanoDark.copy(alpha = overlayAlphas.first),
                                VolcanoMidnight.copy(alpha = overlayAlphas.second),
                                VolcanoDark.copy(alpha = overlayAlphas.third)
                            )
                        )
                    } else {
                        Brush.verticalGradient(listOf(VolcanoDark, VolcanoDark))
                    }
                )
        )
        content()
    }
}

package app.krafted.volcanotimeline.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VolcanoColorScheme = darkColorScheme(
    primary = LavaOrange,
    onPrimary = Color.White,
    primaryContainer = LavaBorder,
    onPrimaryContainer = EmberGold,
    secondary = EmberAmber,
    onSecondary = VolcanoDark,
    secondaryContainer = Color(0xFF4A2800),
    onSecondaryContainer = EmberGold,
    tertiary = MagmaPurple,
    onTertiary = Color.White,
    background = VolcanoDeep,
    onBackground = AshWhite,
    surface = VolcanoMidnight,
    onSurface = AshWhite,
    surfaceVariant = VolcanoNavy,
    onSurfaceVariant = AshGray,
    error = EruptionRed,
    onError = Color.White,
    errorContainer = EruptionRedDark,
    onErrorContainer = Color(0xFFFFCDD2),
    outline = AshDark,
    outlineVariant = Color(0xFF2A2A3A),
    scrim = Color(0xFF000000),
)

@Composable
fun VolcanoTimelineTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = VolcanoColorScheme,
        typography = Typography,
        content = content
    )
}

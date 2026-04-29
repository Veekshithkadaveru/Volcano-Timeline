package app.krafted.volcanotimeline.ui.util

import androidx.compose.ui.graphics.Color
import app.krafted.volcanotimeline.ui.theme.CraterGreen
import app.krafted.volcanotimeline.ui.theme.EmberGold
import app.krafted.volcanotimeline.ui.theme.EruptionRed
import app.krafted.volcanotimeline.ui.theme.GlowOrange
import app.krafted.volcanotimeline.ui.theme.MagmaCrimson
import app.krafted.volcanotimeline.ui.theme.MagmaPurple

fun difficultyColor(difficulty: String): Color = when (difficulty.lowercase()) {
    "easy"   -> CraterGreen
    "medium" -> GlowOrange
    "hard"   -> EruptionRed
    "expert" -> MagmaPurple
    "master" -> MagmaCrimson
    "legend" -> EmberGold
    else     -> Color.White
}

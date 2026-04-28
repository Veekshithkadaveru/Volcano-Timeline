package app.krafted.volcanotimeline.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.volcanotimeline.data.model.EruptionRound
import kotlinx.coroutines.delay

@Composable
fun RoundIntroScreen(
    round: EruptionRound,
    onBegin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showTitle by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }
    val iconScale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(300)
        showTitle = true
        iconScale.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
        delay(400)
        showDetails = true
        delay(400)
        showButton = true
    }

    val context = LocalContext.current
    val backgroundResId = try {
        val resId = context.resources.getIdentifier(round.backgroundKey, "drawable", context.packageName)
        if (resId != 0) resId else null
    } catch (_: Exception) { null }

    val symbolResId = try {
        val resId = context.resources.getIdentifier(round.symbolKey, "drawable", context.packageName)
        if (resId != 0) resId else null
    } catch (_: Exception) { null }

    val eraDescription = when (round.roundId) {
        1 -> "From the devastating blast of Mount St. Helens to the global reach of Hunga Tonga — test your knowledge of eruptions that shaped the modern world."
        2 -> "The 19th century saw some of history's most catastrophic volcanic events. Can you put these titans of destruction in the right order?"
        3 -> "Venture into the age of exploration, where volcanic fury reshaped civilizations and altered global climates for years."
        4 -> "From the legendary Thera eruption to the medieval Samalas blast — ancient and medieval eruptions that echo through history."
        5 -> "Journey millions of years into the past. These prehistoric supereruptions dwarfed anything in recorded history."
        6 -> "The ultimate challenge. Eruptions spanning all of human history — from 41,000 BC to 2018. Only true experts survive."
        else -> "Prepare for the challenge ahead."
    }

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
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.7f),
                            Color(0xFF1A1A2E).copy(alpha = 0.9f),
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(visible = showTitle, enter = fadeIn(tween(600))) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (symbolResId != null) {
                        Image(
                            painter = painterResource(id = symbolResId),
                            contentDescription = null,
                            modifier = Modifier
                                .size(120.dp)
                                .scale(iconScale.value)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    Text(
                        text = "ROUND ${round.roundId}",
                        color = Color(0xFFFFAB40),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = round.title,
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        lineHeight = 42.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedVisibility(
                visible = showDetails,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 2 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val difficultyColor = when (round.difficulty.lowercase()) {
                        "easy" -> Color(0xFF4CAF50)
                        "medium" -> Color(0xFFFFA726)
                        "hard" -> Color(0xFFEF5350)
                        "expert" -> Color(0xFFAB47BC)
                        "master" -> Color(0xFFE53935)
                        "legend" -> Color(0xFFFFD54F)
                        else -> Color.White
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(difficultyColor.copy(alpha = 0.2f))
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = round.difficulty.uppercase(),
                            color = difficultyColor,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            letterSpacing = 2.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = eraDescription,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "${round.eruptions.size} eruptions to order",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            AnimatedVisibility(visible = showButton, enter = fadeIn(tween(400))) {
                Button(
                    onClick = onBegin,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
                ) {
                    Text("BEGIN 🌋", fontSize = 20.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }
            }
        }
    }
}

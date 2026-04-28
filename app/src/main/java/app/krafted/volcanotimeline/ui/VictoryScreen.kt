package app.krafted.volcanotimeline.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.volcanotimeline.R
import kotlinx.coroutines.delay

@Composable
fun VictoryScreen(
    totalScore: Int,
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showEmoji by remember { mutableStateOf(false) }
    var showTitle by remember { mutableStateOf(false) }
    var showScore by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }
    val emojiScale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(400)
        showEmoji = true
        emojiScale.animateTo(1f, tween(700, easing = FastOutSlowInEasing))
        delay(300)
        showTitle = true
        delay(400)
        showScore = true
        delay(400)
        showButton = true
    }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.volk_back_5),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0D0D1A).copy(alpha = 0.88f),
                        Color(0xFF1A1A2E).copy(alpha = 0.92f),
                        Color(0xFF0D0D1A).copy(alpha = 0.95f)
                    )
                )
            )
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(visible = showEmoji, enter = fadeIn(tween(500)) + scaleIn(tween(500))) {
                Text(
                    text = "🌋🏆🌋",
                    fontSize = 64.sp,
                    modifier = Modifier.scale(emojiScale.value)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(visible = showTitle, enter = fadeIn(tween(600))) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "VICTORY!",
                        color = Color(0xFFFFD54F),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 4.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "All six rounds conquered!",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "You are a true volcanic scholar 🔥",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(visible = showScore, enter = fadeIn(tween(500))) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFFD54F).copy(alpha = 0.15f), Color(0xFFFFA726).copy(alpha = 0.15f))
                            )
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("TOTAL SCORE", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        val animatedScore by animateFloatAsState(
                            targetValue = totalScore.toFloat(),
                            animationSpec = tween(1500, easing = FastOutSlowInEasing),
                            label = "totalScore"
                        )
                        Text("${animatedScore.toInt()}", color = Color(0xFFFFD54F), fontSize = 56.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            AnimatedVisibility(visible = showButton, enter = fadeIn(tween(400))) {
                Button(
                    onClick = onHome,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
                ) {
                    Text("HOME", fontSize = 18.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }
            }
        }
    }
}

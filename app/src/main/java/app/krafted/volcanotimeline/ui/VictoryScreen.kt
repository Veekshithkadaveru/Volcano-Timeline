package app.krafted.volcanotimeline.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.volcanotimeline.R
import kotlinx.coroutines.delay

private val confettiColors = listOf(
    Color(0xFFFFD54F), Color(0xFFE65100), Color(0xFFFF6D00), Color(0xFFFFAB40),
    Color(0xFFFFA726), Color(0xFFFFD54F), Color(0xFFEF5350), Color(0xFFFFAB40),
    Color(0xFFFF6D00), Color(0xFFFFD54F), Color(0xFFFFA726), Color(0xFFE65100),
    Color(0xFFFFD54F), Color(0xFFFF6D00), Color(0xFFFFAB40), Color(0xFFEF5350),
    Color(0xFFFFA726), Color(0xFFFFD54F), Color(0xFFE65100), Color(0xFFFF6D00)
)

private val confettiData = listOf(
    Triple(Offset(0.05f, 0.04f), 3.5f, 0),
    Triple(Offset(0.92f, 0.07f), 4f, 1),
    Triple(Offset(0.24f, 0.02f), 3f, 2),
    Triple(Offset(0.76f, 0.05f), 5f, 0),
    Triple(Offset(0.5f, 0.01f), 3.5f, 1),
    Triple(Offset(0.14f, 0.16f), 4f, 2),
    Triple(Offset(0.88f, 0.21f), 3f, 0),
    Triple(Offset(0.42f, 0.09f), 5f, 1),
    Triple(Offset(0.63f, 0.15f), 3.5f, 2),
    Triple(Offset(0.07f, 0.34f), 4f, 0),
    Triple(Offset(0.79f, 0.37f), 3f, 1),
    Triple(Offset(0.33f, 0.27f), 5f, 2),
    Triple(Offset(0.96f, 0.44f), 3.5f, 0),
    Triple(Offset(0.56f, 0.03f), 4f, 1),
    Triple(Offset(0.17f, 0.41f), 3f, 2),
    Triple(Offset(0.69f, 0.31f), 5f, 0),
    Triple(Offset(0.37f, 0.19f), 3.5f, 1),
    Triple(Offset(0.86f, 0.51f), 4f, 2),
    Triple(Offset(0.11f, 0.54f), 3f, 0),
    Triple(Offset(0.47f, 0.47f), 5f, 1)
)

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
    val emojiScale = remember { Animatable(1.6f) }

    LaunchedEffect(Unit) {
        delay(350)
        showEmoji = true
        emojiScale.animateTo(
            1f,
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
        delay(300)
        showTitle = true
        delay(380)
        showScore = true
        delay(380)
        showButton = true
    }

    val infiniteTransition = rememberInfiniteTransition(label = "victory")
    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            tween(2800, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "a1"
    )
    val alpha2 by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 0f,
        animationSpec = infiniteRepeatable(
            tween(2200, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "a2"
    )
    val alpha3 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            tween(3400, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "a3"
    )
    val drift1 by infiniteTransition.animateFloat(
        initialValue = -12f, targetValue = 8f,
        animationSpec = infiniteRepeatable(
            tween(2000, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "d1"
    )
    val drift2 by infiniteTransition.animateFloat(
        initialValue = 10f, targetValue = -15f,
        animationSpec = infiniteRepeatable(
            tween(2600, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "d2"
    )
    val titleHue by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(1400, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "titleHue"
    )
    val animatedTitleColor =
        androidx.compose.ui.graphics.lerp(Color(0xFFFFD54F), Color(0xFFFFAB40), titleHue)

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.volk_back_5),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF0A0A14).copy(alpha = 0.9f),
                            Color(0xFF1A1A2E).copy(alpha = 0.88f),
                            Color(0xFF0A0A14).copy(alpha = 0.95f)
                        )
                    )
                )
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val alphaGroups = listOf(alpha1, alpha2, alpha3)
            val drifts = listOf(drift1, drift2, drift1 * 0.5f)
            confettiData.forEach { (pos, radius, group) ->
                val color = confettiColors[(pos.x * 100).toInt() % confettiColors.size]
                    .copy(alpha = alphaGroups[group])
                val dy = drifts[group] * density
                drawCircle(
                    color = color,
                    radius = radius * density,
                    center = Offset(pos.x * size.width, pos.y * size.height + dy)
                )
                if (group == 0) {
                    drawRect(
                        color = color.copy(alpha = alphaGroups[group] * 0.5f),
                        topLeft = Offset(
                            pos.x * size.width - 2 * density,
                            pos.y * size.height + dy - 5 * density
                        ),
                        size = Size(4 * density, 10 * density)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = showEmoji,
                enter = fadeIn(tween(400)) + scaleIn(
                    spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
                    initialScale = 1.6f
                )
            ) {
                Text(
                    text = "🌋🏆🌋",
                    fontSize = 64.sp,
                    modifier = Modifier.graphicsLayer {
                        scaleX = emojiScale.value; scaleY = emojiScale.value
                    }
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            AnimatedVisibility(
                visible = showTitle,
                enter = fadeIn(tween(500)) + scaleIn(
                    spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow),
                    initialScale = 0.75f
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "VICTORY!",
                        color = animatedTitleColor,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 5.sp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(2.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color.Transparent,
                                        animatedTitleColor,
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "All six rounds conquered!",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "You are a true volcanic scholar 🔥",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            AnimatedVisibility(
                visible = showScore,
                enter = fadeIn(tween(450)) + scaleIn(
                    spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow),
                    initialScale = 0.78f
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFFD54F), Color(0xFFFFA726), Color(0xFFFFD54F))
                            )
                        )
                        .padding(2.5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF1E1E30), Color(0xFF16213E))
                                )
                            )
                            .padding(vertical = 24.dp, horizontal = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "TOTAL SCORE",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 3.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            val animatedScore by animateFloatAsState(
                                targetValue = totalScore.toFloat(),
                                animationSpec = tween(1600, easing = FastOutSlowInEasing),
                                label = "totalScore"
                            )
                            Text(
                                text = "${animatedScore.toInt()}",
                                color = Color(0xFFFFD54F),
                                fontSize = 60.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            AnimatedVisibility(
                visible = showButton,
                enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 2 }
            ) {
                val homeSource = remember { MutableInteractionSource() }
                val homePressed by homeSource.collectIsPressedAsState()
                val homeScale by animateFloatAsState(
                    targetValue = if (homePressed) 0.96f else 1f,
                    animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh),
                    label = "homeScale"
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp)
                        .graphicsLayer { scaleX = homeScale; scaleY = homeScale }
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFFE65100), Color(0xFFFF6D00)))
                        )
                        .clickable(interactionSource = homeSource, indication = null) { onHome() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🏠   HOME",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}

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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.volcanotimeline.viewmodel.GameViewModel
import kotlinx.coroutines.delay

private val emberColors = listOf(
    Color(0xFFFFD54F), Color(0xFFE65100), Color(0xFFFFAB40),
    Color(0xFFFF6D00), Color(0xFFFFA726), Color(0xFFFFD54F),
    Color(0xFFEF5350), Color(0xFFFF6D00), Color(0xFFFFAB40),
    Color(0xFFFFD54F), Color(0xFFE65100), Color(0xFFFFA726)
)

private val emberPositions = listOf(
    Offset(0.08f, 0.12f), Offset(0.88f, 0.08f), Offset(0.28f, 0.06f),
    Offset(0.72f, 0.18f), Offset(0.52f, 0.04f), Offset(0.18f, 0.28f),
    Offset(0.92f, 0.33f), Offset(0.44f, 0.1f), Offset(0.66f, 0.26f),
    Offset(0.06f, 0.44f), Offset(0.76f, 0.4f), Offset(0.34f, 0.22f)
)

@Composable
fun RoundCompleteScreen(
    viewModel: GameViewModel,
    onNextRound: () -> Unit,
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val round = uiState.currentRound ?: return

    val isPerfect = uiState.results.all { it } && uiState.attemptNumber == 1
    val correctCount = uiState.results.count { it }

    var showContent by remember { mutableStateOf(false) }
    var showScore by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }
    val trophyScale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(250)
        showContent = true
        trophyScale.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
        )
        delay(350)
        showScore = true
        delay(400)
        showButtons = true
    }

    val infiniteTransition = rememberInfiniteTransition(label = "complete")
    val emberAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "ember1"
    )
    val emberAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "ember2"
    )
    val emberAlpha3 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.65f,
        animationSpec = infiniteRepeatable(tween(2800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "ember3"
    )
    val emberDrift by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -18f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "drift"
    )
    val perfectBadgePulse by infiniteTransition.animateFloat(
        initialValue = 0.95f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "badgePulse"
    )

    val context = LocalContext.current
    val backgroundResId = try {
        val resId = context.resources.getIdentifier(round.backgroundKey, "drawable", context.packageName)
        if (resId != 0) resId else null
    } catch (_: Exception) { null }

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
                            Color(0xFF0A0A14).copy(alpha = 0.87f),
                            Color(0xFF1A1A2E).copy(alpha = 0.93f),
                            Color(0xFF0A0A14).copy(alpha = 0.92f)
                        )
                    )
                )
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val alphas = listOf(emberAlpha1, emberAlpha2, emberAlpha3)
            emberPositions.forEachIndexed { i, pos ->
                val alpha = alphas[i % 3]
                val color = emberColors[i % emberColors.size].copy(alpha = alpha)
                val yShift = if (i % 2 == 0) emberDrift * density else -emberDrift * density * 0.6f
                drawCircle(
                    color = color,
                    radius = (2.5f + (i % 4)) * density,
                    center = Offset(pos.x * size.width, pos.y * size.height + yShift)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 44.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(tween(450)) + scaleIn(
                    spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow),
                    initialScale = 0.7f
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isPerfect) "🌋" else "🔥",
                        fontSize = 76.sp,
                        modifier = Modifier.scale(trophyScale.value)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isPerfect) "PERFECT!" else "ROUND COMPLETE",
                        color = if (isPerfect) Color(0xFFFFD54F) else Color(0xFFFFAB40),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 3.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = round.title,
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            AnimatedVisibility(
                visible = showScore,
                enter = fadeIn(tween(400)) + scaleIn(
                    spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow),
                    initialScale = 0.8f
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1E1E30), Color(0xFF16213E))
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(22.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.5.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color.Transparent, Color(0xFFE65100).copy(0.6f), Color.Transparent)
                                )
                            )
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "ROUND SCORE",
                        color = Color.White.copy(alpha = 0.45f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 3.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    val animatedScore by animateFloatAsState(
                        targetValue = uiState.score.toFloat(),
                        animationSpec = tween(1100, easing = FastOutSlowInEasing),
                        label = "score"
                    )
                    Text(
                        text = "${animatedScore.toInt()}",
                        color = Color(0xFFFFD54F),
                        fontSize = 60.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatChip("Correct", "$correctCount / ${uiState.cardOrder.size}", Color(0xFF66BB6A))
                        StatChip("Attempts", "${uiState.attemptNumber}", Color(0xFFFFA726))
                        StatChip("Difficulty", round.difficulty.replaceFirstChar { it.uppercase() }, Color(0xFF64B5F6))
                    }
                    if (isPerfect) {
                        Spacer(modifier = Modifier.height(18.dp))
                        Box(
                            modifier = Modifier
                                .graphicsLayer { scaleX = perfectBadgePulse; scaleY = perfectBadgePulse }
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.horizontalGradient(listOf(Color(0xFFFFD54F), Color(0xFFFFA726), Color(0xFFFFD54F)))
                                )
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "⭐  +500 PERFECT BONUS  ⭐",
                                color = Color(0xFF1A1A2E),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            AnimatedVisibility(
                visible = showButtons,
                enter = fadeIn(tween(350)) + slideInVertically(tween(350)) { it / 2 }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (round.roundId < 6) {
                        val nextSource = remember { MutableInteractionSource() }
                        val nextPressed by nextSource.collectIsPressedAsState()
                        val nextScale by animateFloatAsState(
                            targetValue = if (nextPressed) 0.96f else 1f,
                            animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh),
                            label = "nextScale"
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(62.dp)
                                .graphicsLayer { scaleX = nextScale; scaleY = nextScale }
                                .clip(RoundedCornerShape(20.dp))
                                .background(Brush.horizontalGradient(listOf(Color(0xFFE65100), Color(0xFFFF6D00))))
                                .clickable(interactionSource = nextSource, indication = null) { onNextRound() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "NEXT ROUND   →",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.5.sp
                            )
                        }
                    }
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
                            .height(52.dp)
                            .graphicsLayer { scaleX = homeScale; scaleY = homeScale }
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.White.copy(alpha = 0.07f))
                            .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(18.dp))
                            .clickable(interactionSource = homeSource, indication = null) { onHome() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "HOME",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatChip(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.45f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

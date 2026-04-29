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
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import app.krafted.volcanotimeline.data.model.Eruption
import app.krafted.volcanotimeline.game.OrderingEngine
import app.krafted.volcanotimeline.viewmodel.GameViewModel
import kotlinx.coroutines.delay

@Composable
fun ResultScreen(
    viewModel: GameViewModel,
    onNextRound: () -> Unit,
    onTryAgain: () -> Unit,
    onRoundComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val round = uiState.currentRound ?: return
    val correctOrder = remember(round) { OrderingEngine.getSortedByYear(round.eruptions) }

    val slotVisible = remember(uiState.cardOrder.size) {
        mutableStateListOf<Boolean>().apply { repeat(uiState.cardOrder.size) { add(false) } }
    }
    val activeFunFactIndex = remember { mutableIntStateOf(-1) }
    var funFactText by remember { mutableStateOf("") }
    var allRevealed by remember { mutableStateOf(false) }
    var statsVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        statsVisible = true
        for (i in uiState.cardOrder.indices) {
            delay(280L)
            if (i < slotVisible.size) slotVisible[i] = true
        }
        delay(500L)
        allRevealed = true
    }

    val infiniteTransition = rememberInfiniteTransition(label = "results")
    val titleColor by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(1800, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "titleColor"
    )
    val animatedTitleColor =
        androidx.compose.ui.graphics.lerp(Color(0xFFFFAB40), Color(0xFFFFD54F), titleColor)

    val context = LocalContext.current
    val backgroundResId = try {
        val resId =
            context.resources.getIdentifier(round.backgroundKey, "drawable", context.packageName)
        if (resId != 0) resId else null
    } catch (_: Exception) {
        null
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
                .background(Color(0xFF0A0A14).copy(alpha = 0.82f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "RESULTS",
                color = animatedTitleColor,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 5.sp
            )
            Box(
                modifier = Modifier
                    .width(60.dp)
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

            Spacer(modifier = Modifier.height(14.dp))

            val correctCount = uiState.results.count { it }
            AnimatedVisibility(
                visible = statsVisible,
                enter = fadeIn(tween(400)) + scaleIn(
                    spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow),
                    initialScale = 0.85f
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(18.dp))
                        .padding(horizontal = 24.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$correctCount / ${uiState.cardOrder.size}",
                                color = Color(0xFF66BB6A),
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "CORRECT",
                                color = Color.White.copy(alpha = 0.45f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp)
                                .background(Color.White.copy(alpha = 0.12f))
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${uiState.score}",
                                color = Color(0xFFFFD54F),
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "SCORE",
                                color = Color.White.copy(alpha = 0.45f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            uiState.cardOrder.forEachIndexed { index, eruption ->
                val isCorrect = uiState.results.getOrElse(index) { false }
                val fromLeft = index % 2 == 0

                AnimatedVisibility(
                    visible = slotVisible.getOrElse(index) { false },
                    enter = fadeIn(tween(380)) +
                            scaleIn(
                                spring(
                                    Spring.DampingRatioMediumBouncy,
                                    Spring.StiffnessMediumLow
                                ), 0.88f
                            ) +
                            slideInHorizontally(
                                spring(
                                    Spring.DampingRatioLowBouncy,
                                    Spring.StiffnessMediumLow
                                )
                            ) {
                                if (fromLeft) -it / 2 else it / 2
                            }
                ) {
                    ResultSlotCard(
                        eruption = eruption,
                        slotIndex = index,
                        isCorrect = isCorrect,
                        correctEruption = correctOrder.getOrNull(index),
                        showFunFact = activeFunFactIndex.intValue == index,
                        funFactDisplayText = if (activeFunFactIndex.intValue == index) funFactText else "",
                        onTapFunFact = {
                            if (activeFunFactIndex.intValue == index) {
                                activeFunFactIndex.intValue = -1
                                funFactText = ""
                            } else {
                                activeFunFactIndex.intValue = index
                                funFactText = ""
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            if (activeFunFactIndex.intValue >= 0) {
                val targetFact =
                    uiState.cardOrder.getOrNull(activeFunFactIndex.intValue)?.funFact ?: ""
                LaunchedEffect(activeFunFactIndex.intValue) {
                    funFactText = ""
                    for (i in targetFact.indices) {
                        funFactText = targetFact.substring(0, i + 1)
                        delay(18L)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = allRevealed,
                enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 2 }
            ) {
                val allCorrect = uiState.results.all { it }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val actionSource = remember { MutableInteractionSource() }
                    val actionPressed by actionSource.collectIsPressedAsState()
                    val actionScale by animateFloatAsState(
                        targetValue = if (actionPressed) 0.96f else 1f,
                        animationSpec = spring(
                            Spring.DampingRatioMediumBouncy,
                            Spring.StiffnessHigh
                        ),
                        label = "actionScale"
                    )
                    if (allCorrect) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .graphicsLayer { scaleX = actionScale; scaleY = actionScale }
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFF2E7D32),
                                            Color(0xFF4CAF50)
                                        )
                                    )
                                )
                                .clickable(
                                    interactionSource = actionSource,
                                    indication = null
                                ) { onRoundComplete() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "CONTINUE   →",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.5.sp
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .graphicsLayer { scaleX = actionScale; scaleY = actionScale }
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFFE65100),
                                            Color(0xFFFF6D00)
                                        )
                                    )
                                )
                                .clickable(
                                    interactionSource = actionSource,
                                    indication = null
                                ) { onTryAgain() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "TRY AGAIN",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.5.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun ResultSlotCard(
    eruption: Eruption,
    slotIndex: Int,
    isCorrect: Boolean,
    correctEruption: Eruption?,
    showFunFact: Boolean,
    funFactDisplayText: String,
    onTapFunFact: () -> Unit
) {
    val cardScale = remember { Animatable(0.82f) }
    LaunchedEffect(Unit) {
        cardScale.animateTo(
            1f,
            spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)
        )
    }

    val bgColor = if (isCorrect) Color(0xFF1B5E20).copy(alpha = 0.4f)
    else Color(0xFFB71C1C).copy(alpha = 0.32f)

    val positionLabel = when (slotIndex) {
        0 -> "1st"
        1 -> "2nd"
        2 -> "3rd"
        3 -> "4th"
        else -> "${slotIndex + 1}th"
    }

    val borderBrush = if (isCorrect)
        Brush.horizontalGradient(listOf(Color(0xFF4CAF50), Color(0xFF81C784), Color(0xFF4CAF50)))
    else
        Brush.horizontalGradient(listOf(Color(0xFFEF5350), Color(0xFFE57373), Color(0xFFEF5350)))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .scale(cardScale.value)
            .clip(RoundedCornerShape(18.dp))
            .background(bgColor)
            .padding(2.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(listOf(Color(0xFF1A1A2E), Color(0xFF16213E)))
            )
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCorrect) Brush.radialGradient(
                            listOf(
                                Color(0xFF66BB6A),
                                Color(0xFF2E7D32)
                            )
                        )
                        else Brush.radialGradient(listOf(Color(0xFFEF5350), Color(0xFFC62828)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isCorrect) "✓" else "✗",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = eruption.volcano,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp,
                    lineHeight = 21.sp
                )
                Text(
                    text = eruption.country,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    letterSpacing = 0.3.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = eruption.displayYear,
                    color = if (isCorrect) Color(0xFF81C784) else Color(0xFFEF9A9A),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
                Text(
                    text = "$positionLabel slot",
                    color = Color.White.copy(alpha = 0.35f),
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }

        if (!isCorrect && correctEruption != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFFD54F).copy(alpha = 0.08f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "✓ Correct: ${correctEruption.volcano} (${correctEruption.displayYear})",
                    color = Color(0xFFFFD54F),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        val factSource = remember { MutableInteractionSource() }
        val factPressed by factSource.collectIsPressedAsState()
        val factScale by animateFloatAsState(
            targetValue = if (factPressed) 0.97f else 1f,
            animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh),
            label = "factScale"
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { scaleX = factScale; scaleY = factScale }
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFFAB40).copy(alpha = 0.1f))
                .border(1.dp, Color(0xFFFFAB40).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .clickable(interactionSource = factSource, indication = null) { onTapFunFact() }
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (showFunFact) "▲  Hide Fun Fact" else "🌋  Show Fun Fact",
                color = Color(0xFFFFAB40),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
        }

        if (showFunFact && funFactDisplayText.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.04f))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = funFactDisplayText,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

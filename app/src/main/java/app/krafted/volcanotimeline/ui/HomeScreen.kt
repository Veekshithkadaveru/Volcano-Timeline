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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.volcanotimeline.R
import app.krafted.volcanotimeline.viewmodel.HomeViewModel
import app.krafted.volcanotimeline.viewmodel.RoundCardState
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onRoundSelected: (Int) -> Unit,
    onLeaderboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val cardVisible = remember { mutableStateListOf(false, false, false, false, false, false) }
    val titleScale = remember { Animatable(0.6f) }

    val infiniteTransition = rememberInfiniteTransition(label = "home")
    val emojiPulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            tween(1400, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "emojiPulse"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            tween(1600, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    LaunchedEffect(uiState.roundCards) {
        if (uiState.roundCards.isEmpty()) return@LaunchedEffect
        titleScale.animateTo(
            1f,
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
        for (i in uiState.roundCards.indices) {
            delay(150L)
            if (i < cardVisible.size) cardVisible[i] = true
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.volk_back_1),
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
                            Color(0xFF0A0A14).copy(alpha = 0.93f),
                            Color(0xFF1A1A2E).copy(alpha = 0.86f),
                            Color(0xFF0A0A14).copy(alpha = 0.96f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            Box(
                modifier = Modifier.scale(titleScale.value),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(160.dp)) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(
                                Color(0xFFE65100).copy(alpha = glowAlpha),
                                Color.Transparent
                            )
                        ),
                        radius = size.minDimension / 2f
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🌋",
                        fontSize = 56.sp,
                        modifier = Modifier.scale(emojiPulse)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "VOLCANO",
                        color = Color(0xFFFFAB40),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 6.sp
                    )
                    Text(
                        text = "TIMELINE",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Light,
                        letterSpacing = 10.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(1.5.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color(0xFFE65100).copy(0.6f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.totalScore > 0) {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(500)) + scaleIn(
                        spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFFFFD54F).copy(alpha = 0.12f),
                                        Color(0xFFFFAB40).copy(alpha = 0.08f)
                                    )
                                )
                            )
                            .border(
                                1.dp,
                                Color(0xFFFFD54F).copy(alpha = 0.35f),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "⭐  Total Score: ${uiState.totalScore}",
                            color = Color(0xFFFFD54F),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                val leaderboardSource = remember { MutableInteractionSource() }
                val leaderboardPressed by leaderboardSource.collectIsPressedAsState()
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = if (leaderboardPressed) 0.92f else 1f; scaleY =
                            if (leaderboardPressed) 0.92f else 1f
                        }
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFFFFD54F).copy(0.15f),
                                    Color(0xFFFFA726).copy(0.1f)
                                )
                            )
                        )
                        .border(
                            1.dp,
                            Color(0xFFFFD54F).copy(alpha = 0.35f),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable(
                            interactionSource = leaderboardSource,
                            indication = null
                        ) { onLeaderboard() }
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🏆", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SCORES",
                            color = Color(0xFFFFD54F),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            uiState.roundCards.forEachIndexed { index, cardState ->
                val fromLeft = index % 2 == 0
                AnimatedVisibility(
                    visible = cardVisible.getOrElse(index) { false },
                    enter = fadeIn(tween(350)) +
                            scaleIn(
                                spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow),
                                initialScale = 0.92f
                            ) +
                            slideInHorizontally(
                                spring(
                                    Spring.DampingRatioLowBouncy,
                                    Spring.StiffnessMediumLow
                                )
                            ) {
                                if (fromLeft) -it / 3 else it / 3
                            }
                ) {
                    RoundCard(
                        cardState = cardState,
                        onClick = {
                            if (cardState.isUnlocked) onRoundSelected(cardState.round.roundId)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
private fun RoundCard(
    cardState: RoundCardState,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val symbolResId = try {
        val resId = context.resources.getIdentifier(
            cardState.round.symbolKey,
            "drawable",
            context.packageName
        )
        if (resId != 0) resId else null
    } catch (_: Exception) {
        null
    }

    val difficultyColor = when (cardState.round.difficulty.lowercase()) {
        "easy" -> Color(0xFF4CAF50)
        "medium" -> Color(0xFFFFA726)
        "hard" -> Color(0xFFEF5350)
        "expert" -> Color(0xFFAB47BC)
        "master" -> Color(0xFFE53935)
        "legend" -> Color(0xFFFFD54F)
        else -> Color.White
    }

    val infiniteTransition = rememberInfiniteTransition(label = "card_${cardState.round.roundId}")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            tween(1400 + cardState.round.roundId * 120, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "cardBorder_${cardState.round.roundId}"
    )

    val borderColor = when {
        cardState.isCompleted -> Color(0xFF4CAF50)
        cardState.isUnlocked -> Color(0xFFE65100).copy(alpha = borderAlpha)
        else -> Color.White.copy(alpha = 0.1f)
    }

    val cardAlpha = if (cardState.isUnlocked) 1f else 0.42f

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh),
        label = "pressScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
                shadowElevation = if (cardState.isUnlocked && !cardState.isCompleted) 8f else 2f
            }
            .clip(RoundedCornerShape(18.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF1A1A2E), Color(0xFF16213E))
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = cardState.isUnlocked
            ) { onClick() }
            .padding(16.dp)
    ) {
        if (cardState.isUnlocked && !cardState.isCompleted) {
            Box(
                modifier = Modifier
                    .size(width = 3.dp, height = 56.dp)
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color(0xFFE65100).copy(borderAlpha * 0.7f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = if (cardState.isUnlocked && !cardState.isCompleted) 8.dp else 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (symbolResId != null) {
                Box(contentAlignment = Alignment.Center) {
                    if (cardState.isUnlocked && !cardState.isCompleted) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(
                                            Color(0xFFE65100).copy(borderAlpha * 0.25f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                    }
                    Image(
                        painter = painterResource(id = symbolResId),
                        contentDescription = null,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        alpha = cardAlpha
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2A2A4A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${cardState.round.roundId}",
                        color = Color.White.copy(alpha = cardAlpha),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ROUND ${cardState.round.roundId}",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = cardState.round.title,
                    color = Color.White.copy(alpha = cardAlpha),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(5.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(difficultyColor.copy(alpha = 0.14f))
                        .border(
                            0.5.dp,
                            difficultyColor.copy(alpha = 0.3f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 9.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = cardState.round.difficulty.uppercase(),
                        color = difficultyColor.copy(alpha = if (cardState.isUnlocked) 1f else 0.5f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
                when {
                    cardState.isCompleted -> {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2E7D32)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✓",
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        if (cardState.bestScore > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${cardState.bestScore}",
                                color = Color(0xFFFFD54F),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    !cardState.isUnlocked -> {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.06f))
                                .padding(8.dp)
                        ) {
                            Text(text = "🔒", fontSize = 18.sp)
                        }
                    }

                    else -> {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0xFFFF6D00), Color(0xFFE65100))
                                    )
                                )
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "PLAY",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

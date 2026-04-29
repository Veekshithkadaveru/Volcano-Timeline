package app.krafted.volcanotimeline.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import app.krafted.volcanotimeline.ui.components.DraggableEruptionCard
import app.krafted.volcanotimeline.ui.components.OrderSlot
import app.krafted.volcanotimeline.viewmodel.GameViewModel
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onConfirmed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val round = uiState.currentRound

    if (round == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                color = Color(0xFFE65100),
                strokeWidth = 3.dp
            )
        }
        return
    }

    val context = LocalContext.current
    val backgroundResId = try {
        val resId =
            context.resources.getIdentifier(round.backgroundKey, "drawable", context.packageName)
        if (resId != 0) resId else null
    } catch (e: Exception) {
        null
    }

    val difficultyColor = when (round.difficulty.lowercase()) {
        "easy" -> Color(0xFF4CAF50)
        "medium" -> Color(0xFFFFA726)
        "hard" -> Color(0xFFEF5350)
        "expert" -> Color(0xFFAB47BC)
        "master" -> Color(0xFFE53935)
        "legend" -> Color(0xFFFFD54F)
        else -> Color.White
    }

    val cardVisible = remember(uiState.cardOrder.size) {
        mutableStateListOf<Boolean>().apply { repeat(uiState.cardOrder.size) { add(false) } }
    }
    var headerVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.cardOrder) {
        cardVisible.replaceAll { false }
        headerVisible = false
        confirmVisible = false
        delay(80)
        headerVisible = true
        for (i in uiState.cardOrder.indices) {
            delay(120L)
            if (i < cardVisible.size) cardVisible[i] = true
        }
        delay(150)
        confirmVisible = true
    }

    val infiniteTransition = rememberInfiniteTransition(label = "game")
    val confirmGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(900, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "confirmGlow"
    )

    val confirmSource = remember { MutableInteractionSource() }
    val confirmPressed by confirmSource.collectIsPressedAsState()
    val confirmScale by animateFloatAsState(
        targetValue = if (confirmPressed) 0.96f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh),
        label = "confirmScale"
    )

    Box(modifier = modifier.fillMaxSize()) {
        if (backgroundResId != null) {
            Image(
                painter = painterResource(id = backgroundResId),
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
                                Color(0xFF0A0A14).copy(alpha = 0.7f),
                                Color(0xFF0A0A14).copy(alpha = 0.55f),
                                Color(0xFF0A0A14).copy(alpha = 0.75f)
                            )
                        )
                    )
            )
        } else {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0A14)))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = headerVisible,
                enter = fadeIn(tween(300)) + slideInVertically(tween(350)) { -it }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF0D0D1A).copy(0.85f), Color(0xFF16213E).copy(0.8f))
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.07f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "ROUND ${round.roundId}",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(difficultyColor.copy(alpha = 0.2f))
                                        .border(
                                            0.5.dp,
                                            difficultyColor.copy(0.4f),
                                            RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = round.difficulty.uppercase(),
                                        color = difficultyColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.5.sp
                                    )
                                }
                            }
                            Text(
                                text = "ARRANGE CHRONOLOGICALLY",
                                color = Color.White.copy(alpha = 0.35f),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.5.sp
                            )
                        }

                        if (uiState.attemptNumber > 1) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFFFAB40).copy(alpha = 0.15f))
                                    .border(
                                        0.5.dp,
                                        Color(0xFFFFAB40).copy(0.4f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "ATTEMPT ${uiState.attemptNumber}",
                                    color = Color(0xFFFFAB40),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            var containerHeight by remember { mutableStateOf(0f) }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .onSizeChanged { containerHeight = it.height.toFloat() }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (i in uiState.cardOrder.indices) {
                        OrderSlot(index = i, totalItems = uiState.cardOrder.size)
                    }
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    uiState.cardOrder.forEachIndexed { index, eruption ->
                        key(eruption.id) {
                            AnimatedVisibility(
                                visible = cardVisible.getOrElse(index) { false },
                                enter = fadeIn(tween(300)) +
                                        scaleIn(
                                            spring(
                                                Spring.DampingRatioLowBouncy,
                                                Spring.StiffnessMedium
                                            ), 0.88f
                                        ) +
                                        slideInVertically(
                                            spring(
                                                Spring.DampingRatioLowBouncy,
                                                Spring.StiffnessMedium
                                            )
                                        ) { it / 3 }
                            ) {
                                DraggableEruptionCard(
                                    eruption = eruption,
                                    index = index,
                                    isConfirmed = false,
                                    isCorrect = false,
                                    onSwap = { from, to -> viewModel.swapCards(from, to) },
                                    onDragStateChanged = { id, offset ->
                                        viewModel.setDragState(
                                            id,
                                            offset
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .zIndex(1f),
                                    containerHeight = containerHeight,
                                    totalCards = uiState.cardOrder.size
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            AnimatedVisibility(
                visible = confirmVisible,
                enter = fadeIn(tween(350)) + slideInVertically(tween(350)) { it / 2 }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp)
                        .graphicsLayer {
                            scaleX = confirmScale
                            scaleY = confirmScale
                            shadowElevation = 16f
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFE65100).copy(alpha = confirmGlow * 0.3f))
                            .padding(3.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFE65100), Color(0xFFFF6D00))
                                )
                            )
                            .clickable(interactionSource = confirmSource, indication = null) {
                                viewModel.confirmOrder()
                                onConfirmed()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🌋   CONFIRM ORDER",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }
        }
    }
}

package app.krafted.volcanotimeline.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
    val titleScale = remember { Animatable(0.8f) }

    LaunchedEffect(uiState.roundCards) {
        if (uiState.roundCards.isEmpty()) return@LaunchedEffect
        titleScale.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
        for (i in uiState.roundCards.indices) {
            delay(120L)
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
                            Color(0xFF0D0D1A).copy(alpha = 0.92f),
                            Color(0xFF1A1A2E).copy(alpha = 0.88f),
                            Color(0xFF0D0D1A).copy(alpha = 0.95f)
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

            Box(modifier = Modifier.scale(titleScale.value)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🌋",
                        fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "VOLCANO",
                        color = Color(0xFFFFAB40),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 6.sp
                    )
                    Text(
                        text = "TIMELINE",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Light,
                        letterSpacing = 8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.totalScore > 0) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Total Score: ${uiState.totalScore}",
                        color = Color(0xFFFFD54F),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onLeaderboard) {
                    Text(text = "🏆", fontSize = 24.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            uiState.roundCards.forEachIndexed { index, cardState ->
                AnimatedVisibility(
                    visible = cardVisible.getOrElse(index) { false },
                    enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 3 }
                ) {
                    RoundCard(
                        cardState = cardState,
                        onClick = {
                            if (cardState.isUnlocked) {
                                onRoundSelected(cardState.round.roundId)
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))
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
        val resId = context.resources.getIdentifier(cardState.round.symbolKey, "drawable", context.packageName)
        if (resId != 0) resId else null
    } catch (_: Exception) { null }

    val difficultyColor = when (cardState.round.difficulty.lowercase()) {
        "easy" -> Color(0xFF4CAF50)
        "medium" -> Color(0xFFFFA726)
        "hard" -> Color(0xFFEF5350)
        "expert" -> Color(0xFFAB47BC)
        "master" -> Color(0xFFE53935)
        "legend" -> Color(0xFFFFD54F)
        else -> Color.White
    }

    val cardAlpha = if (cardState.isUnlocked) 1f else 0.45f
    val borderColor = when {
        cardState.isCompleted -> Color(0xFF4CAF50)
        cardState.isUnlocked -> Color(0xFFE65100)
        else -> Color.White.copy(alpha = 0.15f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(cardAlpha)
            .clip(RoundedCornerShape(16.dp))
            .background(borderColor.copy(alpha = 0.3f))
            .padding(2.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E)
                    )
                )
            )
            .clickable(enabled = cardState.isUnlocked) { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (symbolResId != null) {
                Image(
                    painter = painterResource(id = symbolResId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2A2A4A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${cardState.round.roundId}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Round ${cardState.round.roundId}",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
                Text(
                    text = cardState.round.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(difficultyColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = cardState.round.difficulty.uppercase(),
                        color = difficultyColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                when {
                    cardState.isCompleted -> {
                        Text(text = "✅", fontSize = 24.sp)
                        if (cardState.bestScore > 0) {
                            Text(
                                text = "${cardState.bestScore}",
                                color = Color(0xFFFFD54F),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    !cardState.isUnlocked -> {
                        Text(text = "🔒", fontSize = 24.sp)
                    }
                    else -> {
                        Text(text = "▶", fontSize = 20.sp, color = Color(0xFFE65100))
                    }
                }
            }
        }
    }
}

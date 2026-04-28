package app.krafted.volcanotimeline.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

    val slotVisible = remember { mutableStateListOf(false, false, false, false) }
    val activeFunFactIndex = remember { mutableIntStateOf(-1) }
    var funFactText by remember { mutableStateOf("") }
    var allRevealed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        for (i in 0 until uiState.cardOrder.size.coerceAtMost(4)) {
            delay(400L)
            slotVisible[i] = true
        }
        delay(600L)
        allRevealed = true
    }

    val context = LocalContext.current
    val backgroundResId = try {
        val resId = context.resources.getIdentifier(
            round.backgroundKey, "drawable", context.packageName
        )
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
                .background(Color.Black.copy(alpha = 0.75f))
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
                color = Color(0xFFFFAB40),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val correctCount = uiState.results.count { it }
                Text(
                    text = "$correctCount / ${uiState.cardOrder.size} Correct",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Score: ${uiState.score}",
                    color = Color(0xFFFFD54F),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            uiState.cardOrder.forEachIndexed { index, eruption ->
                val isCorrect = uiState.results.getOrElse(index) { false }

                AnimatedVisibility(
                    visible = slotVisible.getOrElse(index) { false },
                    enter = fadeIn(tween(500))
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

                Spacer(modifier = Modifier.height(12.dp))
            }

            if (activeFunFactIndex.intValue >= 0) {
                val targetFact = uiState.cardOrder.getOrNull(activeFunFactIndex.intValue)?.funFact ?: ""
                LaunchedEffect(activeFunFactIndex.intValue) {
                    funFactText = ""
                    for (i in targetFact.indices) {
                        funFactText = targetFact.substring(0, i + 1)
                        delay(20L)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = allRevealed,
                enter = fadeIn(tween(400))
            ) {
                val allCorrect = uiState.results.all { it }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (allCorrect) {
                        Button(
                            onClick = onRoundComplete,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E7D32)
                            )
                        ) {
                            Text(
                                text = "CONTINUE",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    } else {
                        Button(
                            onClick = onTryAgain,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE65100)
                            )
                        ) {
                            Text(
                                text = "TRY AGAIN",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
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
    val bgColor = if (isCorrect) Color(0xFF1B5E20).copy(alpha = 0.5f)
    else Color(0xFFB71C1C).copy(alpha = 0.4f)

    val borderBrush = if (isCorrect) {
        Brush.horizontalGradient(listOf(Color(0xFF4CAF50), Color(0xFF81C784)))
    } else {
        Brush.horizontalGradient(listOf(Color(0xFFEF5350), Color(0xFFE57373)))
    }

    val positionLabel = when (slotIndex) {
        0 -> "1st"
        1 -> "2nd"
        2 -> "3rd"
        3 -> "4th"
        else -> "${slotIndex + 1}th"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .padding(2.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1A1A2E))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCorrect) Color(0xFF4CAF50) else Color(0xFFEF5350)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isCorrect) "✓" else "✗",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = eruption.volcano,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = eruption.country,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp
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
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp
                )
            }
        }

        if (!isCorrect && correctEruption != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Correct: ${correctEruption.volcano} (${correctEruption.displayYear})",
                color = Color(0xFFFFD54F),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onTapFunFact,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (showFunFact) "Hide Fun Fact" else "Show Fun Fact 🌋",
                color = Color(0xFFFFAB40),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (showFunFact && funFactDisplayText.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = funFactDisplayText,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

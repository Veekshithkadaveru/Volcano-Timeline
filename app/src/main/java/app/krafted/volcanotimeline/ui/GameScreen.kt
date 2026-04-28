package app.krafted.volcanotimeline.ui

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import app.krafted.volcanotimeline.ui.components.DraggableEruptionCard
import app.krafted.volcanotimeline.ui.components.OrderSlot
import app.krafted.volcanotimeline.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onNavigateNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val round = uiState.currentRound

    if (round == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val context = LocalContext.current
    val backgroundResId = try {
        val resName = round.backgroundKey
        val resId = context.resources.getIdentifier(
            resName, "drawable", context.packageName
        )
        if (resId != 0) resId else null
    } catch (e: Exception) {
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

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
            )
        } else {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.DarkGray))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Round ${round.roundId}: ${round.difficulty.uppercase()}",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Score: ${uiState.score}",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (i in 0 until 4) {
                        OrderSlot(index = i)
                    }
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    uiState.cardOrder.forEachIndexed { index, eruption ->
                        val isCorrect = if (uiState.isConfirmed && uiState.results.size > index) {
                            uiState.results[index]
                        } else false

                        DraggableEruptionCard(
                            eruption = eruption,
                            index = index,
                            isConfirmed = uiState.isConfirmed,
                            isCorrect = isCorrect,
                            onSwap = { from, to -> viewModel.swapCards(from, to) },
                            onDragStateChanged = { id, offset -> viewModel.setDragState(id, offset) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .zIndex(if (uiState.isConfirmed) 0f else 1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!uiState.isConfirmed) {
                Button(
                    onClick = { viewModel.confirmOrder() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
                ) {
                    Text(text = "CONFIRM", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                val allCorrect = uiState.results.all { it }
                if (allCorrect) {
                    Button(
                        onClick = onNavigateNext,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text(text = "NEXT", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { viewModel.nextAttempt() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                    ) {
                        Text(text = "TRY AGAIN", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

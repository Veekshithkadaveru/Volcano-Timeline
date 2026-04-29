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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.volcanotimeline.R
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
        delay(250)
        showTitle = true
        iconScale.animateTo(
            1f,
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
        delay(350)
        showDetails = true
        delay(350)
        showButton = true
    }

    val infiniteTransition = rememberInfiniteTransition(label = "intro")
    val ringAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            tween(1800, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "ring1"
    )
    val ringAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            tween(2400, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "ring2"
    )
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = -7f,
        targetValue = 7f,
        animationSpec = infiniteRepeatable(
            tween(900, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "bounce"
    )

    val context = LocalContext.current
    val backgroundResId = try {
        val resId =
            context.resources.getIdentifier(round.backgroundKey, "drawable", context.packageName)
        if (resId != 0) resId else null
    } catch (_: Exception) {
        null
    }

    val symbolResId = try {
        val resId =
            context.resources.getIdentifier(round.symbolKey, "drawable", context.packageName)
        if (resId != 0) resId else null
    } catch (_: Exception) {
        null
    }

    val eraDescription = when (round.roundId) {
        1 -> stringResource(R.string.era_description_1)
        2 -> stringResource(R.string.era_description_2)
        3 -> stringResource(R.string.era_description_3)
        4 -> stringResource(R.string.era_description_4)
        5 -> stringResource(R.string.era_description_5)
        6 -> stringResource(R.string.era_description_6)
        else -> stringResource(R.string.era_description_default)
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
                            Color(0xFF0A0A14).copy(alpha = 0.75f),
                            Color(0xFF1A1A2E).copy(alpha = 0.88f),
                            Color(0xFF0A0A14).copy(alpha = 0.9f)
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
            AnimatedVisibility(
                visible = showTitle,
                enter = fadeIn(tween(500)) + scaleIn(
                    spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow),
                    initialScale = 0.72f
                ) + slideInVertically(tween(500)) { it / 4 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (symbolResId != null) {
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(148.dp)
                                    .clip(CircleShape)
                                    .border(
                                        2.dp,
                                        Color(0xFFE65100).copy(alpha = ringAlpha2),
                                        CircleShape
                                    )
                            )
                            Box(
                                modifier = Modifier
                                    .size(136.dp)
                                    .clip(CircleShape)
                                    .border(
                                        1.5.dp,
                                        Color(0xFFE65100).copy(alpha = ringAlpha1),
                                        CircleShape
                                    )
                            )
                            Box(
                                modifier = Modifier
                                    .size(128.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(
                                                Color(0xFFE65100).copy(ringAlpha1 * 0.3f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                            Image(
                                painter = painterResource(id = symbolResId),
                                contentDescription = round.title,
                                modifier = Modifier
                                    .size(120.dp)
                                    .scale(iconScale.value)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.height(28.dp))
                    }

                    Text(
                        text = "ROUND ${round.roundId}",
                        color = Color(0xFFFFAB40),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = round.title,
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        lineHeight = 40.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = showDetails,
                enter = fadeIn(tween(450)) + slideInVertically(tween(450)) { it / 2 }
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
                            .background(difficultyColor.copy(alpha = 0.18f))
                            .border(
                                1.dp,
                                difficultyColor.copy(alpha = 0.5f),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 22.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = round.difficulty.uppercase(),
                            color = difficultyColor,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            letterSpacing = 3.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.04f))
                            .border(
                                1.dp,
                                Color.White.copy(alpha = 0.08f),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 18.dp, vertical = 14.dp)
                    ) {
                        Text(
                            text = eraDescription,
                            color = Color.White.copy(alpha = 0.78f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 23.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.06f))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "🗺  ${round.eruptions.size} eruptions to place",
                                color = Color.White.copy(alpha = 0.55f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val btnSource = remember { MutableInteractionSource() }
                    val btnPressed by btnSource.collectIsPressedAsState()
                    val btnScale by animateFloatAsState(
                        targetValue = if (btnPressed) 0.95f else 1f,
                        animationSpec = spring(
                            Spring.DampingRatioMediumBouncy,
                            Spring.StiffnessHigh
                        ),
                        label = "btnScale"
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(62.dp)
                            .graphicsLayer { scaleX = btnScale; scaleY = btnScale }
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFE65100), Color(0xFFFF6D00))
                                )
                            )
                            .clickable(
                                interactionSource = btnSource,
                                indication = null
                            ) { onBegin() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🌋   BEGIN CHALLENGE",
                            color = Color.White,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "↓",
                        color = Color(0xFFFFAB40).copy(alpha = 0.5f),
                        fontSize = 24.sp,
                        modifier = Modifier.graphicsLayer { translationY = bounceOffset }
                    )
                }
            }
        }
    }
}

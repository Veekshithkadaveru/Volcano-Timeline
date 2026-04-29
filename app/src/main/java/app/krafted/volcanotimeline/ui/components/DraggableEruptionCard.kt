package app.krafted.volcanotimeline.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import app.krafted.volcanotimeline.data.model.Eruption
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun DraggableEruptionCard(
    eruption: Eruption,
    index: Int,
    isConfirmed: Boolean,
    isCorrect: Boolean,
    onSwap: (Int, Int) -> Unit,
    onDragStateChanged: (String?, Float) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier,
    containerHeight: Float = 0f,
    totalCards: Int = 4
) {
    val coroutineScope = rememberCoroutineScope()
    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    var isDragging by remember { mutableStateOf(false) }

    val currentZIndex = if (isDragging) 10f else 0f

    val dragModifier = if (!isConfirmed) {
        Modifier.pointerInput(index) {
            detectDragGestures(
                onDragStart = {
                    isDragging = true
                    onDragStateChanged(eruption.id, 0f)
                },
                onDragEnd = {
                    val dropY = offset.value.y
                    val cardHeight = size.height.toFloat()
                    val gap = if (containerHeight > 0f) {
                        (containerHeight - totalCards * cardHeight) / (totalCards + 1)
                    } else {
                        0f
                    }
                    val slotDistance = cardHeight + gap
                    val distanceToUse = if (slotDistance > cardHeight) slotDistance else cardHeight

                    val draggedPositions = (dropY / distanceToUse).roundToInt()
                    val targetIndex = (index + draggedPositions).coerceIn(0, totalCards - 1)
                    if (targetIndex != index) {
                        onSwap(index, targetIndex)
                    }
                    isDragging = false
                    onDragStateChanged(null, 0f)
                    coroutineScope.launch {
                        offset.animateTo(
                            Offset.Zero,
                            spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                    }
                },
                onDragCancel = {
                    isDragging = false
                    onDragStateChanged(null, 0f)
                    coroutineScope.launch {
                        offset.animateTo(
                            Offset.Zero,
                            spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                    }
                }
            ) { change, dragAmount ->
                change.consume()
                coroutineScope.launch {
                    offset.snapTo(offset.value + dragAmount)
                    onDragStateChanged(eruption.id, offset.value.y)
                }
            }
        }
    } else {
        Modifier
    }

    EruptionCard(
        eruption = eruption,
        isConfirmed = isConfirmed,
        isCorrect = isCorrect,
        isDragging = isDragging,
        modifier = modifier
            .offset { IntOffset(0, offset.value.y.roundToInt()) }
            .zIndex(currentZIndex)
            .then(dragModifier)
    )
}

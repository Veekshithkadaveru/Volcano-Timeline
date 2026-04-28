package app.krafted.volcanotimeline.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    val zIndex = if (offset.value != Offset.Zero) 1f else 0f

    val dragModifier = if (!isConfirmed) {
        Modifier.pointerInput(Unit) {
            detectDragGestures(
                onDragStart = {
                    onDragStateChanged(eruption.id, 0f)
                },
                onDragEnd = {
                    val dropY = offset.value.y
                    val cardHeight = size.height.toFloat()
                    val draggedPositions = (dropY / cardHeight).roundToInt()
                    val targetIndex = (index + draggedPositions).coerceIn(0, 3)
                    
                    if (targetIndex != index) {
                        onSwap(index, targetIndex)
                    }
                    
                    onDragStateChanged(null, 0f)
                    coroutineScope.launch {
                        offset.animateTo(Offset.Zero, spring())
                    }
                },
                onDragCancel = {
                    onDragStateChanged(null, 0f)
                    coroutineScope.launch {
                        offset.animateTo(Offset.Zero, spring())
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
        modifier = modifier
            .offset { IntOffset(0, offset.value.y.roundToInt()) }
            .zIndex(zIndex)
            .then(dragModifier)
    )
}

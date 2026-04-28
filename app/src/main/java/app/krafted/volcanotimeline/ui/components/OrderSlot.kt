package app.krafted.volcanotimeline.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OrderSlot(
    index: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(8.dp)
            .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .border(2.dp, Color.Gray, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        val positionText = when (index) {
            0 -> "1st (Oldest)"
            1 -> "2nd"
            2 -> "3rd"
            3 -> "4th (Most Recent)"
            else -> "${index + 1}th"
        }
        Text(
            text = positionText,
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

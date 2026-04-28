package app.krafted.volcanotimeline.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.volcanotimeline.data.model.Eruption

@Composable
fun EruptionCard(
    eruption: Eruption,
    isConfirmed: Boolean,
    isCorrect: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isConfirmed) {
        if (isCorrect) Color.Green else Color.Red
    } else {
        Color.Gray
    }
    
    val backgroundColor = if (isConfirmed) {
        if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = eruption.volcano,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = eruption.country,
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            if (isConfirmed) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = eruption.displayYear,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

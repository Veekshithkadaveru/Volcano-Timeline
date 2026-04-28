package app.krafted.volcanotimeline.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "round_progress")
data class RoundProgress(
    @PrimaryKey
    val roundId: Int,
    val isCompleted: Boolean = false,
    val bestScore: Int = 0,
    val attempts: Int = 0
)

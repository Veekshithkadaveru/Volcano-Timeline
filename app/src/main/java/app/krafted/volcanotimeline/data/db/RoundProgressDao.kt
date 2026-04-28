package app.krafted.volcanotimeline.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RoundProgressDao {
    @Query("SELECT * FROM round_progress WHERE roundId = :roundId")
    fun getRoundProgress(roundId: Int): Flow<RoundProgress?>

    @Query("SELECT * FROM round_progress")
    fun getAllRoundProgress(): Flow<List<RoundProgress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRoundProgress(progress: RoundProgress)
}

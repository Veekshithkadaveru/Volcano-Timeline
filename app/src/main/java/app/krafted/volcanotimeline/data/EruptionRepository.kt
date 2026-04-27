package app.krafted.volcanotimeline.data

import android.content.Context
import app.krafted.volcanotimeline.data.model.EruptionRound
import app.krafted.volcanotimeline.data.model.EruptionsData
import com.google.gson.Gson
import java.io.InputStreamReader

class EruptionRepository(private val context: Context) {

    private var cachedRounds: List<EruptionRound>? = null

    fun getEruptionRounds(): List<EruptionRound> {
        if (cachedRounds != null) return cachedRounds!!

        return try {
            val inputStream = context.assets.open("eruptions.json")
            val reader = InputStreamReader(inputStream)
            val data = Gson().fromJson(reader, EruptionsData::class.java)
            reader.close()
            cachedRounds = data.rounds
            data.rounds
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun getRound(roundId: Int): EruptionRound? {
        return getEruptionRounds().find { it.roundId == roundId }
    }
}

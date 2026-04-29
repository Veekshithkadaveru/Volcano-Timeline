package app.krafted.volcanotimeline.data

import android.content.Context
import app.krafted.volcanotimeline.data.model.EruptionRound
import app.krafted.volcanotimeline.data.model.EruptionsData
import com.google.gson.Gson
import java.io.InputStreamReader

class EruptionRepository(private val context: Context) {

    private var cachedRounds: List<EruptionRound>? = null
    private var loadFailed: Boolean = false

    fun getEruptionRounds(): List<EruptionRound> {
        cachedRounds?.let { return it }

        return try {
            val inputStream = context.assets.open("eruptions.json")
            val reader = InputStreamReader(inputStream)
            val data = Gson().fromJson(reader, EruptionsData::class.java)
            reader.close()
            cachedRounds = data.rounds
            data.rounds
        } catch (e: Exception) {
            loadFailed = true
            emptyList()
        }
    }

    fun hasLoadError() = loadFailed

    fun getRound(roundId: Int): EruptionRound? {
        return getEruptionRounds().find { it.roundId == roundId }
    }
}

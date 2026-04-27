package app.krafted.volcanotimeline.data.model

import com.google.gson.annotations.SerializedName

data class EruptionRound(
    @SerializedName("roundId") val roundId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("difficulty") val difficulty: String,
    @SerializedName("backgroundKey") val backgroundKey: String,
    @SerializedName("symbolKey") val symbolKey: String,
    @SerializedName("eruptions") val eruptions: List<Eruption>
)

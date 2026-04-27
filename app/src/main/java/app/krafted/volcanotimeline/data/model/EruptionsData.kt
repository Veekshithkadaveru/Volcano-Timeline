package app.krafted.volcanotimeline.data.model

import com.google.gson.annotations.SerializedName

data class EruptionsData(
    @SerializedName("rounds") val rounds: List<EruptionRound>
)

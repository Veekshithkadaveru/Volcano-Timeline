package app.krafted.volcanotimeline.data.model

import com.google.gson.annotations.SerializedName

data class Eruption(
    @SerializedName("id") val id: String,
    @SerializedName("volcano") val volcano: String,
    @SerializedName("country") val country: String,
    @SerializedName("year") val year: Int,
    @SerializedName("displayYear") val displayYear: String,
    @SerializedName("deaths") val deaths: Int,
    @SerializedName("veiIndex") val veiIndex: Int,
    @SerializedName("funFact") val funFact: String
)

package app.krafted.volcanotimeline.ui.util

import android.content.Context

fun Context.resolveDrawable(key: String): Int? {
    if (key.isBlank()) return null
    return try {
        val resId = resources.getIdentifier(key, "drawable", packageName)
        if (resId != 0) resId else null
    } catch (_: Exception) {
        null
    }
}

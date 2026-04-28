package app.krafted.volcanotimeline.game

import app.krafted.volcanotimeline.data.model.Eruption

object OrderingEngine {

    fun getSortedByYear(eruptions: List<Eruption>): List<Eruption> {
        return eruptions.sortedBy { it.year }
    }

    fun validateOrder(userOrder: List<Eruption>, correctOrder: List<Eruption>): List<Boolean> {
        return userOrder.mapIndexed { index, eruption ->

            if (index < correctOrder.size) {
                eruption.id == correctOrder[index].id
            } else {
                false
            }
        }
    }

    fun countCorrect(validation: List<Boolean>): Int {
        return validation.count { it }
    }
}

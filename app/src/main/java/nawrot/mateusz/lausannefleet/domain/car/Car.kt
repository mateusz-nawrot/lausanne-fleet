package nawrot.mateusz.lausannefleet.domain.car

import nawrot.mateusz.lausannefleet.domain.map.Position


data class Car(val id: String, var position: Position, val destination: Position, val route: List<Position>, var step: Int = 0) {

    fun move() {
        step += 1
        val index = step.takeIf { step < route.size } ?: route.lastIndex
        position = route.get(index)
    }

    fun reachedDestination(): Boolean {
        return step >= route.size
    }

}
package nawrot.mateusz.lausannefleet.domain.car

import nawrot.mateusz.lausannefleet.domain.map.Position


data class Car(val id: String, var position: Position, val destination: Position, val route: List<Position>, var step: Int = 0)
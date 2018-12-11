package nawrot.mateusz.lausannefleet.domain.car

import nawrot.mateusz.lausannefleet.domain.map.Position


data class Car(val id: Int, var position: Position, val destination: Position)
package nawrot.mateusz.lausannefleet.domain.map

import nawrot.mateusz.lausannefleet.domain.car.Car


data class MapViewState(val error: String = "", val loading: Boolean = true, val cars: List<Car> = emptyList())
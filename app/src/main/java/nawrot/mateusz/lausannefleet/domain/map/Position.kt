package nawrot.mateusz.lausannefleet.domain.map


data class Position(val lat: Double, val lng: Double) {

    override fun toString(): String {
        return "$lat,$lng"
    }
}
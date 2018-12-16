package nawrot.mateusz.lausannefleet.domain.map


interface MapHelper {

    fun getGooglePlayServicesAvailability(): MapStatus

    fun decodePolyline(polyline: String): List<Position>

}
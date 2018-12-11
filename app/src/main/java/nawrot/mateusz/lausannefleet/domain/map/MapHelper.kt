package nawrot.mateusz.lausannefleet.domain.map


interface MapHelper {

    fun isGooglePlayServicesAvailable(): Boolean

    fun decodePolyline(polyline: String): List<Position>

}
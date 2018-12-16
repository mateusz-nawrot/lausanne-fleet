package nawrot.mateusz.lausannefleet.domain.map


interface MapHelper {

    fun areGooglePlayServicesAvailable(): Boolean

    fun decodePolyline(polyline: String): List<Position>

}
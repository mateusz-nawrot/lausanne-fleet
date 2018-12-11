package nawrot.mateusz.lausannefleet.domain.directions

import com.google.gson.annotations.SerializedName


data class DirectionsResponse(val routes: List<RouteDto>, val status: String) {
    fun mapToPolyline(): String? {
        return routes.firstOrNull()?.polyline?.points
    }
}

data class RouteDto(
        @SerializedName("overview_polyline")
        val polyline: PolylineDto

)

data class PolylineDto(val points: String)
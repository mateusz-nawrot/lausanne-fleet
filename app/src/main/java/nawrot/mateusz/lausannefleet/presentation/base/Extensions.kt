package nawrot.mateusz.lausannefleet.presentation.base

import android.content.Context
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import nawrot.mateusz.lausannefleet.domain.map.Position
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.VectorDrawable
import com.google.android.gms.maps.model.Marker
import nawrot.mateusz.lausannefleet.data.station.FleetStationRepository.Companion.STATION_SUFFIX


fun Position.toLatLng(): LatLng {
    return LatLng(lat, lng)
}

fun Context.getBitmapDescriptor(id: Int): BitmapDescriptor {
    val vectorDrawable = getDrawable(id) as VectorDrawable

    val width = vectorDrawable.intrinsicWidth
    val height = vectorDrawable.intrinsicHeight

    vectorDrawable.setBounds(0, 0, width, height)

    val bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bm)
    vectorDrawable.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bm)
}

fun <T> MutableCollection<T>.removeMatching(predicate: (T) -> Boolean) {
    removeAll(filter(predicate))
}

//station marker's id format is "station:{markerId}" - it is required to distinguish between car and station markers after clicking on them
fun Marker.isStationMarker(): Boolean {
    return (tag as String).contains(STATION_SUFFIX)
}

fun Marker.markerId(): String {
    return tag as String
}

package nawrot.mateusz.lausannefleet.presentation

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.maps.android.PolyUtil
import nawrot.mateusz.lausannefleet.domain.map.MapHelper
import nawrot.mateusz.lausannefleet.domain.map.Position
import javax.inject.Inject


class GoogleMapHelper @Inject constructor(private val context: Context): MapHelper {

    override fun isGooglePlayServicesAvailable(): Boolean {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
    }

    override fun decodePolyline(polyline: String): List<Position> {
        val decoded = PolyUtil.decode(polyline)
        return decoded.map { Position(it.latitude, it.longitude) }
    }
}
package nawrot.mateusz.lausannefleet.presentation.base

import com.google.android.gms.maps.model.LatLng
import nawrot.mateusz.lausannefleet.domain.map.Position


fun Position.toLatLng(): LatLng {
    return LatLng(lat, lng)
}
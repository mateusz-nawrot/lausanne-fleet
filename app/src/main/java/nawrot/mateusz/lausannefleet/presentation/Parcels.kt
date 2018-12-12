package nawrot.mateusz.lausannefleet.presentation

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CarMarkerParcel(val id: String, val marker: MarkerOptions) : Parcelable

@Parcelize
data class PolylineParcel(val id: String, val list: List<LatLng>) : Parcelable

@Parcelize
data class StationMarkerParcel(val id: String, val position: MarkerOptions) : Parcelable
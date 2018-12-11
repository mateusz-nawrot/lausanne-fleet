package nawrot.mateusz.lausannefleet.presentation.map

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import dagger.android.AndroidInjection
import nawrot.mateusz.lausannefleet.R
import javax.inject.Inject
import kotlin.math.ln

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MapViewModel

    private var map: GoogleMap? = null

    private val points = mutableListOf<LatLng>()

    var movingMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        initViewModel()
        initMap()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Add a marker in Sydney and move the camera
        val station = LatLng(46.517202, 6.629205)
        val station2 = LatLng(46.541050, 6.658185)

//        map?.addMarker(MarkerOptions().position(station).title("Station 1"))
        map?.addMarker(MarkerOptions().position(station2).title("Station 2"))

        map?.moveCamera(CameraUpdateFactory.newLatLng(station))
        map?.animateCamera(CameraUpdateFactory.zoomTo(13f))

        movingMarker = map?.addMarker(MarkerOptions().position(station).title("Station 1"))

        val line = "al|zGaxmg@VyDBCBGBSGQCAEo@@m@TgIRmFJwEBmBNiGNoDh@yCBUCg@E[MSsAmBa@g@}@i@iGuDOGk@pAa@rAkBnFMxAm@rA_@z@_AzAi@Yc@{@Ma@KyAOeAQk@_@[YI_@@O@WQQSS]@YHe@Va@t@w@NO@o@Ee@O[SQUE}FUU@_@Hw@b@c@V[HWAmCs@qA[qB[wDk@eASaA[cAc@i@]Y[g@o@yJyPMQo@[cAc@WG_@Co@@g@Fi@N_@@_CE?GGIGCGBEH[Ge@We@_@a@i@Uq@M}AKaACGMG_AEYS_EmAo@Uc@Yi@e@g@k@{AoBsAuA{DcDqC}B}AuA_CuBg@i@_AsAc@cAe@_Bm@qDo@sFiAaJa@iDdAYTIJM"

        addPolyline(line, Color.RED)

        for (point in points) {
            val delay: Long = ((points.indexOf(point)+1) * 100).toLong()
            Handler().postDelayed({
                movingMarker?.position = point
                Log.d("MOVING_MARKER", "moving marker to $point")
            }, delay)
        }
    }

    private fun initMap() {
        if (isMapServiceAvailable()) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
            mapFragment?.getMapAsync(this)
        } else {
            GoogleApiAvailability.getInstance().showErrorDialogFragment(this, GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this), 100)
        }
    }

    private fun isMapServiceAvailable(): Boolean {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MapViewModel::class.java)
    }

    private fun addPolyline(polyline: String, color: Int) {
        val decoded = PolyUtil.decode(polyline)
        points.addAll(decoded)
        map?.addPolyline(PolylineOptions().addAll(decoded).width(5f).color(color))
    }
}

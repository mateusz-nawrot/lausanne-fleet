package nawrot.mateusz.lausannefleet.presentation.map

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
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
import nawrot.mateusz.lausannefleet.domain.car.ActionType
import nawrot.mateusz.lausannefleet.domain.car.CarAction
import nawrot.mateusz.lausannefleet.presentation.base.toLatLng
import javax.inject.Inject

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MapViewModel

    private lateinit var map: GoogleMap

    private val points = mutableListOf<LatLng>()

    private val markers = ArrayList<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        initViewModel()
        initMap()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val station = LatLng(46.517202, 6.629205)
        val station2 = LatLng(46.541050, 6.658185)

        map.addMarker(MarkerOptions().position(station2).title("Station 2"))

        map.moveCamera(CameraUpdateFactory.newLatLng(station))
        map.animateCamera(CameraUpdateFactory.zoomTo(13f))

        onStationClicked("1")
//
//        val line = "al|zGaxmg@VyDBCBGBSGQCAEo@@m@TgIRmFJwEBmBNiGNoDh@yCBUCg@E[MSsAmBa@g@}@i@iGuDOGk@pAa@rAkBnFMxAm@rA_@z@_AzAi@Yc@{@Ma@KyAOeAQk@_@[YI_@@O@WQQSS]@YHe@Va@t@w@NO@o@Ee@O[SQUE}FUU@_@Hw@b@c@V[HWAmCs@qA[qB[wDk@eASaA[cAc@i@]Y[g@o@yJyPMQo@[cAc@WG_@Co@@g@Fi@N_@@_CE?GGIGCGBEH[Ge@We@_@a@i@Uq@M}AKaACGMG_AEYS_EmAo@Uc@Yi@e@g@k@{AoBsAuA{DcDqC}B}AuA_CuBg@i@_AsAc@cAe@_Bm@qDo@sFiAaJa@iDdAYTIJM"

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
        map.addPolyline(PolylineOptions().addAll(decoded).width(5f).color(color))
    }

    private fun onStationClicked(stationId: String) {
        viewModel.addCar(stationId).observe(this, Observer { car -> car?.apply { handleCarAction(this) } })
    }

    private fun handleCarAction(carAction: CarAction) {
        Log.d("CAR_ACTION", carAction.toString())
        when(carAction.actionType) {
            ActionType.ADD -> {
                val newMarker = map.addMarker(MarkerOptions().position(carAction.car.position.toLatLng()))
                newMarker.tag = carAction.car.id.toString()
                markers.add(newMarker)
            }
            ActionType.MOVE -> {
                markers.find { it.tag == carAction.car.id.toString()}?.position = carAction.car.position.toLatLng()
            }
            ActionType.REMOVE -> {
                markers.find { it.tag == carAction.car.id.toString()}?.remove()
            }
        }
    }
}

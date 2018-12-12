package nawrot.mateusz.lausannefleet.presentation.map

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.android.AndroidInjection
import nawrot.mateusz.lausannefleet.R
import nawrot.mateusz.lausannefleet.domain.base.ErrorEvent
import nawrot.mateusz.lausannefleet.domain.car.ActionType
import nawrot.mateusz.lausannefleet.domain.car.Car
import nawrot.mateusz.lausannefleet.domain.car.CarAction
import nawrot.mateusz.lausannefleet.domain.station.Station
import nawrot.mateusz.lausannefleet.presentation.base.getBitmapDescriptor
import nawrot.mateusz.lausannefleet.presentation.base.toLatLng
import javax.inject.Inject

const val POLYLINE_WIDTH = 6f

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MapViewModel

    private lateinit var map: GoogleMap

    private val carMarkers = ArrayList<Marker>()
    private val stationMarkers = ArrayList<Marker>()
    private val routes = ArrayList<Polyline>()

    private val carIcon by lazy { getBitmapDescriptor(R.drawable.ic_car) }
    private val blueMarkerIcon by lazy { BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE) }
    private val redMarkerIcon by lazy { BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED) }

    private var menuRef: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        initViewModel()
        initMap()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_menu, menu)
        menuRef = menu
        return super.onCreateOptionsMenu(menu)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMarkerClickListener(this)
        map.setOnMapClickListener(this)
        map.moveCamera(CameraUpdateFactory.newLatLng(LatLng(46.522873, 6.635265)))
        map.animateCamera(CameraUpdateFactory.zoomTo(12f))

        viewModel.getStations()
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        marker?.apply {
            val markerTag = (tag as String)
            if (markerTag.contains("station")) {
                val id = markerTag.split(":")[1]
                deselectAllMarkers()
                marker.setIcon(redMarkerIcon)

                menuRef?.findItem(R.id.send_car)?.apply {
                    isVisible = true
                    setOnMenuItemClickListener {
                        sendCarToStation(id)
                    true
                    }
                }
            }
        }
        return false
    }

    override fun onMapClick(point: LatLng?) {
        menuRef?.findItem(R.id.send_car)?.isVisible = false
        deselectAllMarkers()
    }

    private fun deselectAllMarkers(markerToExclude: String? = null) {
        stationMarkers.forEach { it.setIcon(blueMarkerIcon) }
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MapViewModel::class.java)
        viewModel.error().observe(this, Observer { error -> showError(error) })
        viewModel.stations().observe(this, Observer { stations -> drawStations(stations) })
    }

    private fun sendCarToStation(stationId: String) {
        viewModel.addCar(stationId).observe(this, Observer { car -> car?.apply { handleCarAction(this) } })
    }

    private fun drawStations(stations: List<Station>) {
        for (station in stations) {
            val stationMarker = map.addMarker(MarkerOptions().position(station.position.toLatLng()).icon(blueMarkerIcon))
            stationMarker.tag = "station:${station.id}"
            stationMarkers.add(stationMarker)
        }
    }

    private fun handleCarAction(action: CarAction) {
        when (action.type) {
            ActionType.ADD -> {
                addMarker(action.car)
                addPolyline(action.car)
            }
            ActionType.MOVE -> {
                findCarMarker(action.id)?.position = action.car.position.toLatLng()
            }
            ActionType.REMOVE -> {
                findCarMarker(action.id)?.remove()
                findPolyline(action.id)?.remove()
            }
        }
    }

    private fun showError(errorEvent: ErrorEvent) {
        if (errorEvent.mapError) {
            //TODO - show some dialog/textview?
        }
        //TODO - better error handling...
        Toast.makeText(this, errorEvent.errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun addMarker(car: Car) {
        val newMarker = map.addMarker(MarkerOptions().position(car.position.toLatLng()).icon(carIcon).anchor(0.5f, 0.5f))
        newMarker.tag = car.id
        carMarkers.add(newMarker)
    }

    private fun addPolyline(car: Car) {
        val route = car.route.map { it.toLatLng() }
        val polyline = map.addPolyline(PolylineOptions().addAll(route).width(POLYLINE_WIDTH).color(getColor()))
        polyline.tag = car.id
        routes.add(polyline)
    }

    //TODO add random color
    private fun getColor(): Int {
        return Color.MAGENTA
    }

    private fun findCarMarker(id: String): Marker? {
        return carMarkers.find { it.tag == id }
    }

    private fun findPolyline(id: String): Polyline? {
        return routes.find { it.tag == id }
    }

}

package nawrot.mateusz.lausannefleet.presentation.map

import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import kotlinx.android.synthetic.main.activity_map.*
import nawrot.mateusz.lausannefleet.R
import nawrot.mateusz.lausannefleet.domain.base.ErrorEvent
import nawrot.mateusz.lausannefleet.domain.car.ActionType
import nawrot.mateusz.lausannefleet.domain.car.Car
import nawrot.mateusz.lausannefleet.domain.car.CarEvent
import nawrot.mateusz.lausannefleet.domain.station.Station
import nawrot.mateusz.lausannefleet.presentation.CarMarkerParcel
import nawrot.mateusz.lausannefleet.presentation.PolylineParcel
import nawrot.mateusz.lausannefleet.presentation.StationMarkerParcel
import nawrot.mateusz.lausannefleet.presentation.base.*
import javax.inject.Inject


class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    companion object {
        const val POLYLINE_WIDTH = 6f

        const val KEY_CAR_MARKERS = "carmarkers"
        const val KEY_STATION_MARKERS = "stationmarkers"
        const val KEY_POLYLINES = "polylines"

        const val DEFAULT_MAP_ZOOM = 12f
        val LAUSANNE_LAT_LNG = LatLng(46.521474, 6.612146)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MapViewModel

    private lateinit var map: GoogleMap

    private val carMarkers = ArrayList<Marker>()
    private val stationMarkers = ArrayList<Marker>()
    private val routes = ArrayList<Polyline>()

    private val carIcon by lazy { getBitmapDescriptor(R.drawable.ic_car) }
    private val blueMarkerIcon by lazy { BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE) }
    private val redMarkerIcon by lazy { BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED) }

    //restoring the state after rotation
    private val polylineParcels = arrayListOf<PolylineParcel>()
    private val carParcels = arrayListOf<CarMarkerParcel>()
    private val stationParcels = arrayListOf<StationMarkerParcel>()

    private var menuRef: Menu? = null

    //flag used for marker recreation when map is ready after device rotation
    private var isAfterRotation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MapViewModel::class.java)
        initMap()

        if (savedInstanceState != null) {
            isAfterRotation = true
            polylineParcels.addAll(savedInstanceState.getParcelableArrayList(KEY_POLYLINES))
            carParcels.addAll(savedInstanceState.getParcelableArrayList(KEY_CAR_MARKERS))
            stationParcels.addAll(savedInstanceState.getParcelableArrayList(KEY_STATION_MARKERS))
        } else {
            observeViewModel()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.apply {
            outState.putParcelableArrayList(KEY_CAR_MARKERS, carParcels)
            outState.putParcelableArrayList(KEY_POLYLINES, polylineParcels)
            outState.putParcelableArrayList(KEY_STATION_MARKERS, stationParcels)
        }
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
        showLausanne(map)

        if (isAfterRotation) {
            recreateMarkers()
            observeViewModel()
            //used only for resubscribing to existing cars after device rotation
            viewModel.fleet().forEach { it.observe(this, Observer { car -> handleCarAction(car) }) }
        } else {
            viewModel.getStations()
            viewModel.getFleetInfo()
        }
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        marker?.apply {
            if (this.isStationMarker()) {
                val id = marker.markerId()
                deselectAllMarkers()
                marker.setIcon(redMarkerIcon)

                menuRef?.findItem(R.id.send_car)?.apply {
                    isVisible = true
                    setOnMenuItemClickListener {
                        sendCarToStation(id)
                        true
                    }
                }
                return false
            }
        }
        return true
    }

    override fun onMapClick(point: LatLng?) {
        menuRef?.findItem(R.id.send_car)?.isVisible = false
        deselectAllMarkers()
    }

    private fun deselectAllMarkers() {
        stationMarkers.forEach { it.setIcon(blueMarkerIcon) }
    }

    private fun initMap() {
        //no need to safe cast, R.id.mapFragment will always be SupportMapFragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun observeViewModel() {
        viewModel.viewState().observe(this, Observer { error -> handleErrorEvent(error) })
        viewModel.stations().observe(this, Observer { stations -> drawStations(stations) })

        viewModel.currentCarCount().observe(this, Observer { currentCars -> updateCurrentCarCounter(currentCars) })
        viewModel.totalCarCount().observe(this, Observer { totalCars -> updateTotalCarCounter(totalCars) })
        viewModel.totalTimeSpent().observe(this, Observer { totalTime -> updateTotalTimeSpent(totalTime) })
    }

    private fun sendCarToStation(stationId: String) {
        viewModel.addCar(stationId).observe(this, Observer { car -> car?.apply { handleCarAction(this) } })
    }

    private fun handleCarAction(event: CarEvent) {
        //TODO remove log
        Log.d("CAR_ACTION", event.toString())
        when (event.type) {
            ActionType.ADD -> {
                drawCarMarker(event.car)
                drawPolyline(event.car)
            }
            ActionType.MOVE -> findCarMarker(event.id)?.position = event.car.position.toLatLng()

            ActionType.REMOVE -> {
                findCarMarker(event.id)?.remove()
                findPolyline(event.id)?.remove()

                //rotation purpose
                carParcels.removeMatching { it.id == event.id }
                polylineParcels.removeMatching { it.id == event.id }
            }
        }
    }

    private fun updateCurrentCarCounter(counter: Int) {
        currentCarsValue.text = counter.toString()
    }

    private fun updateTotalCarCounter(counter: Int) {
        totalCarsValue.text = counter.toString()
    }

    private fun updateTotalTimeSpent(time: Long) {
        totalTimeValue.text = time.toString()
    }

    private fun handleErrorEvent(error: ErrorEvent) {
        if (error.mapError) {
            //TODO - show some dialog/textview?
        }
        Toast.makeText(this@MapActivity, error.errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun findCarMarker(id: String): Marker? {
        return carMarkers.find { it.tag == id }
    }

    private fun findPolyline(id: String): Polyline? {
        return routes.find { it.tag == id }
    }

    private fun drawStations(stations: List<Station>) {
        for (station in stations) {
            val stationMarkerOptions = MarkerOptions().position(station.position.toLatLng()).icon(blueMarkerIcon)
            addStationMarker(station.id, stationMarkerOptions)
        }
    }

    private fun drawCarMarker(car: Car) {
        val markerOptions = MarkerOptions().position(car.position.toLatLng()).icon(carIcon).anchor(0.5f, 0.5f).zIndex(1f)
        addCarMarker(car.id, markerOptions)
    }

    private fun drawPolyline(car: Car) {
        val route = car.route.map { it.toLatLng() }
        addPolyline(car.id, route)
    }

    private fun addStationMarker(id: String, markerOptions: MarkerOptions) {
        val stationMarker = map.addMarker(markerOptions)
        stationMarker.tag = id
        stationMarkers.add(stationMarker)
    }

    private fun addCarMarker(id: String, markerOptions: MarkerOptions, recreatingFromState: Boolean = false) {
        val newMarker = map.addMarker(markerOptions)
        newMarker.tag = id
        carMarkers.add(newMarker)

        //rotation purpose, flag to prevent from adding again when recreating from savedInstanceState
        if (recreatingFromState.not()) {
            carParcels.add(CarMarkerParcel(id, markerOptions))
        }
    }

    private fun addPolyline(id: String, route: List<LatLng>, recreatingFromState: Boolean = false) {
        val polyline = map.addPolyline(PolylineOptions().addAll(route).width(POLYLINE_WIDTH).color(getColor()))
        polyline.tag = id
        routes.add(polyline)

        //rotation purpose, flag to prevent from adding parcels to list again when recreating from savedInstanceState
        if (recreatingFromState.not()) {
            polylineParcels.add(PolylineParcel(id, route))
        }
    }

    private fun recreateMarkers() {
        for (stationParcel in stationParcels) {
            addStationMarker(stationParcel.id, stationParcel.position)
        }

        for (carParcel in carParcels) {
            addCarMarker(carParcel.id, carParcel.marker, true)
        }

        for (polylineParcel in polylineParcels) {
            addPolyline(polylineParcel.id, polylineParcel.list, true)
        }
    }

    private fun showLausanne(map: GoogleMap) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LAUSANNE_LAT_LNG, DEFAULT_MAP_ZOOM))
    }

    //TODO add random color
    private fun getColor(): Int {
        return Color.BLACK
    }

}

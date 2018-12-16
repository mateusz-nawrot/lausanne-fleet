package nawrot.mateusz.lausannefleet.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.reactivex.Observable
import io.reactivex.Single
import nawrot.mateusz.lausannefleet.domain.base.ErrorEvent
import nawrot.mateusz.lausannefleet.domain.car.*
import nawrot.mateusz.lausannefleet.domain.map.MapError
import nawrot.mateusz.lausannefleet.domain.map.MapHelper
import nawrot.mateusz.lausannefleet.domain.map.Position
import nawrot.mateusz.lausannefleet.domain.station.GetStationsUseCase
import nawrot.mateusz.lausannefleet.domain.station.Station
import nawrot.mateusz.lausannefleet.presentation.map.MapViewModel
import nawrot.mateusz.lausannefleet.verifyValues
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.MockitoAnnotations
import java.lang.IllegalStateException
import java.util.concurrent.TimeUnit


class MapViewModelTest {

    @Rule
    @JvmField
    var instantExecutorRule = InstantTaskExecutorRule()

    //test objects
    private val testCurrentCars = 1

    private val testTotalCars = 3

    private val testTimeSpent = 300L

    private val carError = IllegalStateException("You have reached maximum number of cars")

    private val mapErrorMessage = "Your device does not support Google Play services"

    private val testStations = listOf(Station("1", Position(1.0, 1.0)))

    private val testCarEvent = CarEvent("1", ActionType.MOVE, Car("1", Position(1.0, 1.0), Position(2.0, 2.0), listOf()))

    //test observers
    @Mock
    private lateinit var viewStateObserver: Observer<ErrorEvent>

    @Mock
    private lateinit var totalCarsObserver: Observer<Int>

    @Mock
    private lateinit var currentCarsObserver: Observer<Int>

    @Mock
    private lateinit var timeSpentObserver: Observer<Long>

    @Mock
    private lateinit var stationsObserver: Observer<List<Station>>

    @Mock
    private lateinit var fleetObserver: Observer<List<CarEvent>>

    //mocked use cases
    @Mock
    private lateinit var getStationsUseCase: GetStationsUseCase

    @Mock
    private lateinit var addCarUseCase: AddCarUseCase

    @Mock
    private lateinit var getTotalNumberOfCarsUseCase: GetTotalNumberOfCarsUseCase

    @Mock
    private lateinit var getCurrentNumberOfCarsUseCase: GetCurrentNumberOfCarsUseCase

    @Mock
    private lateinit var getTotalTimeSpentUseCase: GetTotalTimeSpentUseCase

    @Mock
    private lateinit var mapHelper: MapHelper

    private lateinit var viewModel: MapViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        viewModel = MapViewModel(
                getStationsUseCase,
                addCarUseCase,
                getTotalNumberOfCarsUseCase,
                getCurrentNumberOfCarsUseCase,
                getTotalTimeSpentUseCase,
                mapHelper
        )

        `when`(getCurrentNumberOfCarsUseCase.execute()).thenReturn(Observable.just(testCurrentCars))
        `when`(getTotalNumberOfCarsUseCase.execute()).thenReturn(Observable.just(testTotalCars))
        `when`(getTotalTimeSpentUseCase.execute()).thenReturn(Observable.just(testTimeSpent))
    }

    @Test
    fun `ViewModel emits stations list when use case succeeds`() {
        `when`(getStationsUseCase.execute()).thenReturn(Single.just(testStations))

        viewModel.stations().observeForever(stationsObserver)
        viewModel.getStations()

        stationsObserver.verifyValues(testStations)
    }

    @Test
    fun `ViewModel emits current car count when use case succeeds`() {
        viewModel.currentCarCount().observeForever(currentCarsObserver)
        viewModel.getFleetInfo()

        currentCarsObserver.verifyValues(testCurrentCars)
    }

    @Test
    fun `ViewModel emits total time spent when use case succeeds`() {
        viewModel.totalCarCount().observeForever(totalCarsObserver)
        viewModel.getFleetInfo()

        totalCarsObserver.verifyValues(testTotalCars)
    }

    @Test
    fun `ViewModel emits total car count when use case succeeds`() {
        viewModel.totalTimeSpent().observeForever(timeSpentObserver)
        viewModel.getFleetInfo()

        timeSpentObserver.verifyValues(testTimeSpent)
    }

    @Test
    fun `ViewModel emits cars fleet with proper size`() {
        //delay is here to prevent Observable from termination after emitting testCarEvent
        val testCarObservable = Observable.just(testCarEvent).delay(3, TimeUnit.SECONDS)

        `when`(addCarUseCase.execute(anyString())).thenReturn(testCarObservable)

        val fleetSize = 3

        for (i in 0 until fleetSize) {
            viewModel.addCar("1")
        }

        val fleet = viewModel.fleet()

        assertEquals(fleetSize, fleet.size)
    }

    @Test
    fun `ViewModel emits error event when reached maximum number of cars`() {
        `when`(addCarUseCase.execute(anyString())).thenReturn(Observable.error(carError))

        viewModel.viewState().observeForever(viewStateObserver)
        viewModel.addCar("1")

        viewStateObserver.verifyValues(ErrorEvent(carError.localizedMessage, -1, false))
    }

    @Test
    fun `ViewModel emits map error event when play services are not available`() {
        `when`(mapHelper.getGooglePlayServicesAvailability()).thenReturn(MapError(1))

        viewModel.viewState().observeForever(viewStateObserver)
        viewModel.checkMapsAvailability()

        viewStateObserver.verifyValues(ErrorEvent(mapErrorMessage, 1, true))
    }
}
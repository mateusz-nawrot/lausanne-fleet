package nawrot.mateusz.lausannefleet.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.reactivex.Observable
import nawrot.mateusz.lausannefleet.domain.base.ErrorEvent
import nawrot.mateusz.lausannefleet.domain.car.*
import nawrot.mateusz.lausannefleet.domain.map.MapHelper
import nawrot.mateusz.lausannefleet.domain.map.Position
import nawrot.mateusz.lausannefleet.domain.station.GetStationsUseCase
import nawrot.mateusz.lausannefleet.presentation.map.MapViewModel
import nawrot.mateusz.lausannefleet.verifyValues
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.MockitoAnnotations
import java.lang.IllegalStateException


class MapViewModelTest {

    @Rule
    @JvmField
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var viewStateObserver: Observer<ErrorEvent>

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

    private val testCarEvent = CarEvent("id", ActionType.MOVE, Car("id", Position(1.0, 1.0), Position(2.0, 2.0), listOf()))

    private val carError = IllegalStateException("You have reached maximum number of cars")

    private val mapErrorMessage = "Your device does not support Google Play services"

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

     `when`(addCarUseCase.execute(anyString())).thenReturn(Observable.just(testCarEvent))
    }

    @Test
    fun `ViewModel emits error event when reached maximum number of cars`() {
        `when`(addCarUseCase.execute(anyString())).thenReturn(Observable.error(carError))

        viewModel.viewState().observeForever(viewStateObserver)

        viewModel.addCar("1")

        viewStateObserver.verifyValues(
            ErrorEvent(carError.localizedMessage, false)
        )
    }

    @Test
    fun `ViewModel emits map error event when play services are not available`() {
        `when`(mapHelper.areGooglePlayServicesAvailable()).thenReturn(false)

        viewModel.viewState().observeForever(viewStateObserver)

        viewModel.checkMapsAvailability()

        viewStateObserver.verifyValues(
            ErrorEvent(mapErrorMessage, true)
        )
    }
}
package nawrot.mateusz.lausannefleet

import io.reactivex.Single
import io.reactivex.subjects.ReplaySubject
import nawrot.mateusz.lausannefleet.data.ApiInterface
import nawrot.mateusz.lausannefleet.data.car.FleetCarRepository
import nawrot.mateusz.lausannefleet.domain.car.ActionType
import nawrot.mateusz.lausannefleet.domain.car.Car
import nawrot.mateusz.lausannefleet.domain.car.CarEvent
import nawrot.mateusz.lausannefleet.domain.car.CarRepository
import nawrot.mateusz.lausannefleet.domain.directions.DirectionsResponse
import nawrot.mateusz.lausannefleet.domain.directions.PolylineDto
import nawrot.mateusz.lausannefleet.domain.directions.RouteDto
import nawrot.mateusz.lausannefleet.domain.map.MapHelper
import nawrot.mateusz.lausannefleet.domain.map.Position
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.spy
import org.mockito.MockitoAnnotations


class CarRepositoryTest {

    @Mock
    private lateinit var apiInterface: ApiInterface

    @Mock
    private lateinit var mapHelper: MapHelper

    private lateinit var carRepository: CarRepository

    private val testOrigin = Position(1.0, 1.0)
    private val testDestination = Position(3.0, 3.0)

    private val testDirections = DirectionsResponse(listOf(RouteDto(PolylineDto("test_polyline"))), "ok")

    private val testRoute = listOf(
            Position(1.0, 1.0),
            Position(2.0, 2.0),
            Position(3.0, 3.0)
    )

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        carRepository = spy(FleetCarRepository(apiInterface, mapHelper))
        `when`(apiInterface.getDirections(testOrigin, testDestination)).thenReturn(Single.just(testDirections))
        `when`(mapHelper.decodePolyline(ArgumentMatchers.anyString())).thenReturn(testRoute)


        `when`(carRepository.getMaximumCarCapacity()).thenReturn(10)
    }

    @Test
    fun `Car repository emits correct CarEvents when new vehicle added to the map`() {
        val testObserver = carRepository.addCar(testOrigin, testDestination).test()

        val expectedCarEvents = arrayOf(
                CarEvent("0", ActionType.ADD, Car("0", testRoute[0], testDestination, testRoute, 0)),
                CarEvent("0", ActionType.MOVE, Car("0", testRoute[1], testDestination, testRoute, 1)),
                CarEvent("0", ActionType.MOVE, Car("0", testRoute[2], testDestination, testRoute, 2)),
                CarEvent("0", ActionType.REMOVE, Car("0", testRoute[2], testDestination, testRoute, 3))
        )

        testObserver.awaitTerminalEvent()
        testObserver.assertNoErrors()
        testObserver.assertValues(*expectedCarEvents)
    }

    @Test
    fun `Car repository emits correct total number of cars`() {
        `when`(carRepository.getCarUpdateIntervalMillis()).thenReturn(1L)
        val numberOfCars = 3

        for (i in 0 until numberOfCars) {
            carRepository.addCar(testOrigin, testDestination).test().awaitTerminalEvent()
        }

        val totalNumberOfCars = (carRepository.getTotalNumberOfCars() as ReplaySubject).values.last()
        assertEquals(numberOfCars, totalNumberOfCars)
    }

    @Test
    fun `Car repository emits correct current number of cars`() {
        //long interval time to prevent first car from termination
        `when`(carRepository.getCarUpdateIntervalMillis()).thenReturn(10000L)

        carRepository.addCar(testOrigin, testDestination).test()

        `when`(carRepository.getCarUpdateIntervalMillis()).thenReturn(10L)

        val observerToBeTerminated = carRepository.addCar(testOrigin, testDestination).test()

        var currentNumberOfCars = (carRepository.getCurrentNumberOfCars() as ReplaySubject).values.last()
        assertEquals(2, currentNumberOfCars)

        observerToBeTerminated.awaitTerminalEvent()

        currentNumberOfCars = (carRepository.getCurrentNumberOfCars() as ReplaySubject).values.last()

        assertEquals(1, currentNumberOfCars)
    }


}
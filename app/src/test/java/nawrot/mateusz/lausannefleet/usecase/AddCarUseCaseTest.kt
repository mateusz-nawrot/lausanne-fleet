package nawrot.mateusz.lausannefleet.usecase

import io.reactivex.ObservableTransformer
import io.reactivex.Single
import nawrot.mateusz.lausannefleet.domain.base.SchedulersProvider
import nawrot.mateusz.lausannefleet.domain.car.AddCarUseCase
import nawrot.mateusz.lausannefleet.domain.car.CarEvent
import nawrot.mateusz.lausannefleet.domain.car.CarRepository
import nawrot.mateusz.lausannefleet.domain.map.Position
import nawrot.mateusz.lausannefleet.domain.station.Station
import nawrot.mateusz.lausannefleet.domain.station.StationRepository
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations


class AddCarUseCaseTest {

    @Mock
    private lateinit var schedulersProvider: SchedulersProvider

    @Mock
    private lateinit var stationRepository: StationRepository

    @Mock
    private lateinit var carRepository: CarRepository

    private lateinit var useCase: AddCarUseCase

    private val testOrigin = Station("1", Position(46.517202, 6.629205))
    private val testDestination = Station("1", Position(46.517202, 6.629205))

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        useCase = AddCarUseCase(schedulersProvider, stationRepository, carRepository)

        `when`(schedulersProvider.observableTransformer<Any>()).thenReturn(ObservableTransformer { it })
        `when`(stationRepository.getTripOrigin("1")).thenReturn(Single.just(testOrigin))
        `when`(stationRepository.getTripDestination("1")).thenReturn(Single.just(testDestination))
    }

    @Test
    fun `CarRepository adds car with correct trip origin and destination when executed`() {
        val testStationId = "1"

        useCase.execute(testStationId).test()

        verify(stationRepository).getTripOrigin(testStationId)
        verify(stationRepository).getTripDestination(testStationId)
        verify(carRepository).addCar(testOrigin.position, testDestination.position)
    }
}
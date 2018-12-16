package nawrot.mateusz.lausannefleet.usecase

import io.reactivex.Single
import io.reactivex.SingleTransformer
import nawrot.mateusz.lausannefleet.domain.base.SchedulersProvider
import nawrot.mateusz.lausannefleet.domain.map.Position
import nawrot.mateusz.lausannefleet.domain.station.GetStationsUseCase
import nawrot.mateusz.lausannefleet.domain.station.Station
import nawrot.mateusz.lausannefleet.domain.station.StationRepository
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations


class GetStationsUseCaseTest {

    @Mock
    private lateinit var schedulersProvider: SchedulersProvider

    @Mock
    private lateinit var stationRepository: StationRepository


    private lateinit var useCase: GetStationsUseCase


    private val testStations = listOf(
        Station("station1", Position(1.0, 1.0)),
        Station("station2", Position(2.0, 2.0)),
        Station("station3", Position(3.0, 3.0))
    )

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        useCase = GetStationsUseCase(schedulersProvider, stationRepository)

        Mockito.`when`(stationRepository.getStations()).thenReturn(Single.just(testStations))
        Mockito.`when`(schedulersProvider.singleTransformer<Any>()).thenReturn(SingleTransformer { it })
    }

    @Test
    fun `GetStationsUseCase returns stations list from repository`() {
        val testObserver = useCase.execute(Unit).test()

        verify(stationRepository).getStations()

        testObserver.assertComplete()
        testObserver.assertNoErrors()
        testObserver.assertValue { it == testStations }
    }

}
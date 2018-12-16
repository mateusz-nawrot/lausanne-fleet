package nawrot.mateusz.lausannefleet.usecase

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import nawrot.mateusz.lausannefleet.domain.base.SchedulersProvider
import nawrot.mateusz.lausannefleet.domain.car.CarRepository
import nawrot.mateusz.lausannefleet.domain.car.GetTotalTimeSpentUseCase
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations


class GetTotalTimeSpentUseCaseTest {

    @Mock
    private lateinit var schedulersProvider: SchedulersProvider

    @Mock
    private lateinit var carRepository: CarRepository

    private lateinit var useCase: GetTotalTimeSpentUseCase

    private val timeSpent = 123L

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        useCase = GetTotalTimeSpentUseCase(schedulersProvider, carRepository)

        Mockito.`when`(schedulersProvider.observableTransformer<Any>()).thenReturn(ObservableTransformer { it })
        `when`(carRepository.getTotalTimeSpent()).thenReturn(Observable.just(timeSpent))

    }

    @Test
    fun `GetTotalTimeSpentUseCase gets total time spent from repository`() {
        val testObserver = useCase.execute().test()

        verify(carRepository).getTotalTimeSpent()

        testObserver.assertComplete()
        testObserver.assertNoErrors()
        testObserver.assertValue { it == timeSpent }
    }
}
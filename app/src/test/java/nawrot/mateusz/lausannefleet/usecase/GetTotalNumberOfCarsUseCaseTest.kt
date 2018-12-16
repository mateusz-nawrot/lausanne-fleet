package nawrot.mateusz.lausannefleet.usecase

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import nawrot.mateusz.lausannefleet.domain.base.SchedulersProvider
import nawrot.mateusz.lausannefleet.domain.car.CarRepository
import nawrot.mateusz.lausannefleet.domain.car.GetTotalNumberOfCarsUseCase
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations


class GetTotalNumberOfCarsUseCaseTest {

    @Mock
    private lateinit var schedulersProvider: SchedulersProvider

    @Mock
    private lateinit var carRepository: CarRepository

    private lateinit var useCase: GetTotalNumberOfCarsUseCase

    private val totalCars = 2

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        useCase = GetTotalNumberOfCarsUseCase(schedulersProvider, carRepository)

        Mockito.`when`(schedulersProvider.observableTransformer<Any>()).thenReturn(ObservableTransformer { it })
        Mockito.`when`(carRepository.getTotalNumberOfCars()).thenReturn(Observable.just(totalCars))
    }

    @Test
    fun `GetTotalNumberOfCarsUseCase gets total number cars from repository`() {
        val testObserver = useCase.execute().test()

        Mockito.verify(carRepository).getTotalNumberOfCars()

        testObserver.assertComplete()
        testObserver.assertNoErrors()
        testObserver.assertValue { it == totalCars }
    }
}
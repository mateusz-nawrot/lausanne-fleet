package nawrot.mateusz.lausannefleet.usecase

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import nawrot.mateusz.lausannefleet.domain.base.SchedulersProvider
import nawrot.mateusz.lausannefleet.domain.car.CarRepository
import nawrot.mateusz.lausannefleet.domain.car.GetCurrentNumberOfCarsUseCase
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations


class GetCurrentNumberOfCarsUseCaseTest {

    @Mock
    private lateinit var schedulersProvider: SchedulersProvider

    @Mock
    private lateinit var carRepository: CarRepository

    private lateinit var useCase: GetCurrentNumberOfCarsUseCase

    private val carsNumber = 2

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        useCase = GetCurrentNumberOfCarsUseCase(schedulersProvider, carRepository)

        Mockito.`when`(schedulersProvider.observableTransformer<Any>()).thenReturn(ObservableTransformer { it })
        Mockito.`when`(carRepository.getCurrentNumberOfCars()).thenReturn(Observable.just(carsNumber))
    }

    @Test
    fun `GetCurrentNumberOfCarsUseCase gets current number cars from repository`() {
        val testObserver = useCase.execute().test()

        verify(carRepository).getCurrentNumberOfCars()

        testObserver.assertComplete()
        testObserver.assertNoErrors()
        testObserver.assertValue { it == carsNumber }
    }

}
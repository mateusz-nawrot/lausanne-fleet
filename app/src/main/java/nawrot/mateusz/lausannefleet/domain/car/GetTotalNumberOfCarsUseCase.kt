package nawrot.mateusz.lausannefleet.domain.car

import io.reactivex.Observable
import nawrot.mateusz.lausannefleet.domain.base.ObservableUnitUseCase
import nawrot.mateusz.lausannefleet.domain.base.SchedulersProvider
import javax.inject.Inject


class GetTotalNumberOfCarsUseCase @Inject constructor(schedulersProvider: SchedulersProvider,
                                                      private val carRepository: CarRepository): ObservableUnitUseCase<Int>(schedulersProvider) {

    override fun createUseCaseObservable(param: Unit): Observable<Int> {
        return carRepository.getTotalNumberOfCars()
    }
}
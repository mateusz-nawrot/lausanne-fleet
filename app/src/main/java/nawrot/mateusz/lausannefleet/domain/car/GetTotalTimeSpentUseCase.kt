package nawrot.mateusz.lausannefleet.domain.car

import io.reactivex.Observable
import nawrot.mateusz.lausannefleet.domain.base.ObservableUnitUseCase
import nawrot.mateusz.lausannefleet.domain.base.SchedulersProvider
import javax.inject.Inject


class GetTotalTimeSpentUseCase @Inject constructor(schedulersProvider: SchedulersProvider,
                                                  private val carRepository: CarRepository): ObservableUnitUseCase<Long>(schedulersProvider) {

    override fun createUseCaseObservable(param: Unit): Observable<Long> {
        return carRepository.getTotalTimeSpent()
    }
}
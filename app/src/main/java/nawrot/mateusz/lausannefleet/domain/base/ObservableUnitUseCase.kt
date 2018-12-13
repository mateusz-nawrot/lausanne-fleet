package nawrot.mateusz.lausannefleet.domain.base

import io.reactivex.Observable


abstract class ObservableUnitUseCase<R>(schedulersProvider: SchedulersProvider) : ObservableUseCase<Unit, R>(schedulersProvider) {

    fun execute(): Observable<R> {
        return execute(Unit)
    }

}
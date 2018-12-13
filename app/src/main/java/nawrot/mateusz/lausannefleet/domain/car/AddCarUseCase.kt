package nawrot.mateusz.lausannefleet.domain.car

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import nawrot.mateusz.lausannefleet.domain.base.ObservableUseCase
import nawrot.mateusz.lausannefleet.domain.base.SchedulersProvider
import nawrot.mateusz.lausannefleet.domain.map.Position
import nawrot.mateusz.lausannefleet.domain.station.StationRepository
import javax.inject.Inject


class AddCarUseCase @Inject constructor(schedulersProvider: SchedulersProvider,
                                        private val stationRepository: StationRepository,
                                        private val carRepository: CarRepository) : ObservableUseCase<String, CarEvent>(schedulersProvider) {

    override fun createUseCaseObservable(stationId: String): Observable<CarEvent> {
        return stationRepository.getTripOrigin(stationId)
                .zipWith(stationRepository.getTripDestination(stationId),
                        BiFunction<Position, Position, Pair<Position, Position>> { origin, destination -> Pair(origin, destination) })
                .flatMapObservable { carRepository.addCar(it.first, it.second) }
    }

}
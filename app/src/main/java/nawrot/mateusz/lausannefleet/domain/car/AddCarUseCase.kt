package nawrot.mateusz.lausannefleet.domain.car

import io.reactivex.Completable
import io.reactivex.functions.BiFunction
import nawrot.mateusz.lausannefleet.domain.base.CompletableUseCase
import nawrot.mateusz.lausannefleet.domain.base.SchedulersProvider
import nawrot.mateusz.lausannefleet.domain.map.Position
import nawrot.mateusz.lausannefleet.domain.station.StationRepository
import javax.inject.Inject


class AddCarUseCase @Inject constructor(schedulersProvider: SchedulersProvider,
                                        private val stationRepository: StationRepository,
                                        private val carRepository: CarRepository) : CompletableUseCase<String>(schedulersProvider) {

    override fun createUseCaseCompletable(stationId: String): Completable {
        return stationRepository.getTripOrigin(stationId)
                .zipWith(stationRepository.getTripDestination(stationId),
                        BiFunction<Position, Position, Pair<Position, Position>> { origin, destination -> Pair(origin, destination) }
                ).flatMapCompletable { carRepository.addCar(it.first, it.second) }
    }

}
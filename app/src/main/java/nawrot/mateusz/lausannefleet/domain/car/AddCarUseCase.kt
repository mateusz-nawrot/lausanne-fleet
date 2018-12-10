package nawrot.mateusz.lausannefleet.domain.car

import io.reactivex.Observable
import nawrot.mateusz.lausannefleet.domain.ObservableUseCase
import nawrot.mateusz.lausannefleet.domain.SchedulersProvider
import nawrot.mateusz.lausannefleet.domain.station.StationRepository
import javax.inject.Inject


class AddCarUseCase @Inject constructor(schedulersProvider: SchedulersProvider,
                                        private val stationRepository: StationRepository,
                                        private val carRepository: CarRepository) : ObservableUseCase<String, List<Car>>(schedulersProvider) {

    override fun createUseCaseObservable(param: String): Observable<List<Car>> {
        return stationRepository.getRandomStationPosition(param).flatMapObservable { carRepository.addCar(it) }
    }
}
package nawrot.mateusz.lausannefleet.data.car

import io.reactivex.Observable
import nawrot.mateusz.lausannefleet.domain.car.Car
import nawrot.mateusz.lausannefleet.domain.car.CarRepository
import nawrot.mateusz.lausannefleet.domain.map.Position
import javax.inject.Inject


class FleetCarRepository @Inject constructor(): CarRepository {

    override fun addCar(position: Position): Observable<List<Car>> {
        TODO()
    }

}
package nawrot.mateusz.lausannefleet.domain.car

import io.reactivex.Observable
import nawrot.mateusz.lausannefleet.domain.map.Position


interface CarRepository {

    fun addCar(position: Position): Observable<List<Car>>

}
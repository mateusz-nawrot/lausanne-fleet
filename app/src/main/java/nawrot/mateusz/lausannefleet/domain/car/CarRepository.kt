package nawrot.mateusz.lausannefleet.domain.car

import io.reactivex.Observable
import nawrot.mateusz.lausannefleet.domain.map.Position


interface CarRepository {

    fun addCar(origin: Position, destination: Position): Observable<CarEvent>

    fun getCurrentNumberOfCars(): Observable<Int>

    fun getTotalNumberOfCars(): Observable<Int>

    fun getTotalTimeSpent(): Observable<Long>

    fun getCarUpdateIntervalMillis(): Long

    fun getMaximumCarCapacity(): Int

}
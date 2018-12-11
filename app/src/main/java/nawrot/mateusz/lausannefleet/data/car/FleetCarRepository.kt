package nawrot.mateusz.lausannefleet.data.car

import io.reactivex.Completable
import io.reactivex.Observable
import nawrot.mateusz.lausannefleet.domain.car.Car
import nawrot.mateusz.lausannefleet.domain.car.CarRepository
import nawrot.mateusz.lausannefleet.domain.map.Position
import java.lang.IllegalStateException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

const val MAXIMUM_CAPACITY = 10

@Singleton
class FleetCarRepository @Inject constructor() : CarRepository {

    private val cars = mutableListOf<Car>()

    override fun addCar(origin: Position, destination: Position): Completable {
        return Completable.create {
            if (canAdd()) {
                cars.add(Car(generateId(), origin, destination))
                it.onComplete()
            } else {
                it.onError(IllegalStateException("You have reached maximum number of cars"))
            }
        }
    }

    override fun getCars(): Observable<List<Car>> {
        return Observable.interval(1, TimeUnit.SECONDS).flatMap { Observable.just(moveCars()) }
    }

    private fun moveCars(): List<Car> {
        return cars
    }

    private fun canAdd(): Boolean {
        return cars.size < MAXIMUM_CAPACITY
    }

    private fun generateId(): Int {
        return cars.size
    }

}
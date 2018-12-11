package nawrot.mateusz.lausannefleet.data.car

import io.reactivex.Observable
import io.reactivex.Single
import nawrot.mateusz.lausannefleet.data.ApiInterface
import nawrot.mateusz.lausannefleet.domain.car.ActionType
import nawrot.mateusz.lausannefleet.domain.car.Car
import nawrot.mateusz.lausannefleet.domain.car.CarAction
import nawrot.mateusz.lausannefleet.domain.car.CarRepository
import nawrot.mateusz.lausannefleet.domain.map.MapHelper
import nawrot.mateusz.lausannefleet.domain.map.Position
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

const val MAXIMUM_CAPACITY = 10

@Singleton
class FleetCarRepository @Inject constructor(private val apiInterface: ApiInterface,
                                             private val mapHelper: MapHelper) : CarRepository {

    private val cars = mutableListOf<Car>()

    private var totalCarsCreated = 0

    private var totalTimeSpent = 0

    override fun addCar(origin: Position, destination: Position): Observable<CarAction> {
        if (!canAdd()) {
            return Observable.error(IllegalStateException("You have reached maximum number of cars"))
        }

        return apiInterface.getDirections(origin, destination).flatMap {
            val polyline = it.mapToPolyline()
            if (polyline == null) {
                Single.error(IllegalStateException("Could not find directions for specified origin and destination points"))
            } else {
                val points = mapHelper.decodePolyline(polyline)
                Single.just(points)
            }
        }.flatMapObservable { route ->
            val newCar = Car(generateId(), origin, destination, route)
            if (cars.add(newCar)) {
                totalCarsCreated.inc()
            }
            Observable
                    .interval(100, TimeUnit.MILLISECONDS)
                    .map { moveCar(cars.first { it.id == newCar.id }) }
                    .startWith(CarAction.add(newCar))
                    .takeUntil() { it.actionType == ActionType.REMOVE }
                    .doOnComplete { cars.remove(newCar) }

        }
    }

    private fun moveCar(car: Car): CarAction {
        car.step = car.step + 1
        car.position = car.route[car.step]
        totalTimeSpent += 1
        return if (car.position == car.route.last()) {
            CarAction.remove(car)
        } else {
            CarAction.move(car)
        }
    }

    private fun canAdd(): Boolean {
        return cars.size < MAXIMUM_CAPACITY
    }

    private fun generateId(): Int {
        return cars.size
    }

}
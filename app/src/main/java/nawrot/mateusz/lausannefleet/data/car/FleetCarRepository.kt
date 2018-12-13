package nawrot.mateusz.lausannefleet.data.car

import io.reactivex.Observable
import io.reactivex.Single
import nawrot.mateusz.lausannefleet.data.ApiInterface
import nawrot.mateusz.lausannefleet.domain.car.ActionType
import nawrot.mateusz.lausannefleet.domain.car.Car
import nawrot.mateusz.lausannefleet.domain.car.CarEvent
import nawrot.mateusz.lausannefleet.domain.car.CarRepository
import nawrot.mateusz.lausannefleet.domain.map.MapHelper
import nawrot.mateusz.lausannefleet.domain.map.Position
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

const val CAR_UPDATE_INTERVAL_MILLIS = 100L
const val MAXIMUM_CAPACITY = 3

@Singleton
class FleetCarRepository @Inject constructor(private val apiInterface: ApiInterface,
                                             private val mapHelper: MapHelper) : CarRepository {

    private val cars = mutableListOf<Car>()

    private var totalCarsCreated = 0
    private var totalTimeSpent = 0

    override fun addCar(origin: Position, destination: Position): Observable<CarEvent> {
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

            cars.add(newCar)
            totalCarsCreated += 1

            Observable
                    .interval(CAR_UPDATE_INTERVAL_MILLIS, TimeUnit.MILLISECONDS)
                    .map { moveCar(cars.first { it.id == newCar.id }) }
                    .startWith(CarEvent.add(newCar))
                    .takeUntil { it.type == ActionType.REMOVE }
                    .doOnComplete { cars.remove(newCar) }
        }
    }

    private fun moveCar(car: Car): CarEvent {
        car.step += 1
        car.position = car.route[car.step]
        totalTimeSpent += 1
        //if it reached last point of the route - it should be removed from map
        return if (car.position == car.route.last()) {
            CarEvent.remove(car)
        } else {
            CarEvent.move(car)
        }
    }

    private fun canAdd(): Boolean {
        return cars.size < MAXIMUM_CAPACITY
    }

    private fun generateId(): String {
        return totalCarsCreated.toString()
    }

}
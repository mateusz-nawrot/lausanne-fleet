package nawrot.mateusz.lausannefleet.data.car

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.ReplaySubject
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


@Singleton
class FleetCarRepository @Inject constructor(private val apiInterface: ApiInterface,
                                             private val mapHelper: MapHelper) : CarRepository {

    companion object {
        const val DEFAULT_CAR_UPDATE_INTERVAL_MILLIS = 100L
        const val DEFAULT_MAXIMUM_CAPACITY = 10

        const val TOTAL_TIME_UPDATE_INTERVAL_MILLIS = 5000L
    }

    private val cars = mutableListOf<Car>()

    private var totalCarsCreated = 0
    private var totalTimeSpentMillis = 0L

    private val totalCarsSubject = ReplaySubject.create<Int>()
    private val currentCarsSubject = ReplaySubject.create<Int>()

    init {
        totalCarsSubject.onNext(0)
        currentCarsSubject.onNext(0)
    }

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

            //update car counters
            currentCarsSubject.onNext(cars.size)
            totalCarsSubject.onNext(totalCarsCreated)

            Observable
                    .interval(getCarUpdateIntervalMillis(), TimeUnit.MILLISECONDS)
                    .map { moveCar(cars.first { it.id == newCar.id }) }
                    .startWith(CarEvent.add(newCar.copy()))
                    .takeUntil { it.type == ActionType.REMOVE }
                    .doOnComplete {
                        cars.remove(newCar)
                        currentCarsSubject.onNext(cars.size)
                    }
        }
    }

    override fun getCurrentNumberOfCars(): Observable<Int> {
        return currentCarsSubject
    }

    override fun getTotalNumberOfCars(): Observable<Int> {
        return totalCarsSubject
    }

    override fun getTotalTimeSpent(): Observable<Long> {
        return Observable
                .interval(TOTAL_TIME_UPDATE_INTERVAL_MILLIS, TimeUnit.MILLISECONDS)
                .map {  TimeUnit.MILLISECONDS.toSeconds(totalTimeSpentMillis) }
                .startWith(0)
    }

    override fun getCarUpdateIntervalMillis(): Long = DEFAULT_CAR_UPDATE_INTERVAL_MILLIS

    override fun getMaximumCarCapacity(): Int = DEFAULT_MAXIMUM_CAPACITY

    private fun moveCar(car: Car): CarEvent {
        car.move()
        totalTimeSpentMillis += getCarUpdateIntervalMillis()
        //if it reached last point of the route - it should be removed from map
        return if (car.reachedDestination()) {
            CarEvent.remove(car.copy())
        } else {
            CarEvent.move(car.copy())
        }
    }

    private fun canAdd(): Boolean {
        return cars.size < getMaximumCarCapacity()
    }

    private fun generateId(): String {
        return totalCarsCreated.toString()
    }

}
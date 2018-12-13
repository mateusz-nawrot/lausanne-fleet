package nawrot.mateusz.lausannefleet.presentation.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import nawrot.mateusz.lausannefleet.domain.base.ErrorEvent
import nawrot.mateusz.lausannefleet.domain.car.*
import nawrot.mateusz.lausannefleet.domain.map.MapHelper
import nawrot.mateusz.lausannefleet.domain.station.GetStationsUseCase
import nawrot.mateusz.lausannefleet.domain.station.Station
import nawrot.mateusz.lausannefleet.presentation.base.BaseViewModel
import nawrot.mateusz.lausannefleet.presentation.base.SingleEventLiveData
import javax.inject.Inject


class MapViewModel @Inject constructor(private val getStationsUseCase: GetStationsUseCase,
                                       private val addCarUseCase: AddCarUseCase,
                                       private val getTotalNumberOfCarsUseCase: GetTotalNumberOfCarsUseCase,
                                       private val getCurrentNumberOfCarsUseCase: GetCurrentNumberOfCarsUseCase,
                                       private val getTotalTimeSpentUseCase: GetTotalTimeSpentUseCase,
                                       private val mapHelper: MapHelper) : BaseViewModel() {

    private val errorStateLiveData = SingleEventLiveData<ErrorEvent>()
    private val stationsLiveData = MutableLiveData<List<Station>>()
    private val currentCarCountLiveData = MutableLiveData<Int>()
    private val totalCarCountLiveData = MutableLiveData<Int>()
    private val timeSpentLiveData = MutableLiveData<Int>()

    private val fleet = arrayListOf<LiveData<CarEvent>>()

    //TODO - use when initialized
    fun checkMapsAvailability() {
        //check map availability - emit error VS if no play services installed
        if (mapHelper.isGooglePlayServicesAvailable().not()) {
            emitError(ErrorEvent("Your device does not support Google Play services", mapError = true))
        }
    }

    fun addCar(stationId: String): LiveData<CarEvent> {
        val carLiveData = MutableLiveData<CarEvent>()
        fleet.add(carLiveData)
        manage(addCarUseCase.execute(stationId)
                .doOnComplete { fleet.remove(carLiveData) }
                .subscribe(
                        { carLiveData.value = it },
                        { error -> emitError(ErrorEvent(error.localizedMessage)) }
                )
        )
        return carLiveData
    }

    fun getStations() {
        manage(getStationsUseCase.execute(Unit).subscribe(
                { stations -> stationsLiveData.value = stations },
                { error -> emitError(ErrorEvent(error.localizedMessage)) }
        ))
    }

    fun getFleetInfo() {
        getTotalCarsInfo()
        getCurrentCarsInfo()
        getTimeSpentInfo()
    }

    private fun getCurrentCarsInfo() {
        manage(getCurrentNumberOfCarsUseCase.execute().subscribe(
                { currentCars -> currentCarCountLiveData.value = currentCars },
                { error -> emitError(ErrorEvent(error.localizedMessage)) }
        ))
    }

    private fun getTotalCarsInfo() {
        manage(getTotalNumberOfCarsUseCase.execute().subscribe(
                { totalCars -> totalCarCountLiveData.value = totalCars },
                { error -> emitError(ErrorEvent(error.localizedMessage)) }
        ))
    }

    private fun getTimeSpentInfo() {
        manage(getTotalTimeSpentUseCase.execute().subscribe(
                { timeSpent -> timeSpentLiveData.value = timeSpent },
                { error -> emitError(ErrorEvent(error.localizedMessage)) }
        ))
    }

    fun fleet(): List<LiveData<CarEvent>> {
        return fleet
    }

    fun stations(): LiveData<List<Station>> {
        return stationsLiveData
    }

    fun viewState(): LiveData<ErrorEvent> {
        return errorStateLiveData
    }

    fun totalTimeSpent(): LiveData<Int> {
        return timeSpentLiveData
    }

    fun currentCarCount(): LiveData<Int> {
        return currentCarCountLiveData
    }

    fun totalCarCount(): LiveData<Int> {
        return totalCarCountLiveData
    }

    private fun emitError(newState: ErrorEvent) {
        errorStateLiveData.value = newState
    }


}
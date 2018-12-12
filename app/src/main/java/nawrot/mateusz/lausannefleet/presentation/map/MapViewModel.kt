package nawrot.mateusz.lausannefleet.presentation.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import nawrot.mateusz.lausannefleet.domain.base.ErrorEvent
import nawrot.mateusz.lausannefleet.domain.car.AddCarUseCase
import nawrot.mateusz.lausannefleet.domain.car.CarAction
import nawrot.mateusz.lausannefleet.domain.map.MapHelper
import nawrot.mateusz.lausannefleet.domain.station.GetStationsUseCase
import nawrot.mateusz.lausannefleet.domain.station.Station
import nawrot.mateusz.lausannefleet.presentation.base.BaseViewModel
import nawrot.mateusz.lausannefleet.presentation.base.SingleEventLiveData
import javax.inject.Inject


class MapViewModel @Inject constructor(private val getStationsUseCase: GetStationsUseCase,
                                       private val addCarUseCase: AddCarUseCase,
                                       private val mapHelper: MapHelper) : BaseViewModel() {

    private val errorLiveData = SingleEventLiveData<ErrorEvent>()
    private val stationsLiveData = MutableLiveData<List<Station>>()
    private val carCountLiveData = MutableLiveData<Int>()
    private val timeSpentLiveData = MutableLiveData<Int>()

    private val fleet = arrayListOf<LiveData<CarAction>>()

    //TODO - use when initialized
    fun checkMapsAvailability() {
        //check map availability - emit error VS if no play services installed
        if (mapHelper.isGooglePlayServicesAvailable().not()) {
            errorLiveData.value = ErrorEvent("Your device does not support Google Play services", mapError = true)
        }
    }

    fun addCar(stationId: String): LiveData<CarAction> {
        val carLiveData = MutableLiveData<CarAction>()
        fleet.add(carLiveData)
        manage(addCarUseCase.execute(stationId).doOnComplete { fleet.remove(carLiveData) }.subscribe(
                { carLiveData.value = it },
                { error -> errorLiveData.value = ErrorEvent(error.localizedMessage) }
        ))
        return carLiveData
    }

    fun fleet(): List<LiveData<CarAction>> {
        return fleet
    }

    fun getStations() {
        manage(getStationsUseCase.execute(Unit).subscribe(
                { stations -> stationsLiveData.value = stations },
                { error -> errorLiveData.value = ErrorEvent(error.localizedMessage) }
        ))
    }

    fun stations(): LiveData<List<Station>> {
        return stationsLiveData
    }

    fun error(): LiveData<ErrorEvent> {
        return errorLiveData
    }

    fun timeSpent(): LiveData<Int> {
        return timeSpentLiveData
    }

    fun carCounter(): LiveData<Int> {
        return carCountLiveData
    }


}
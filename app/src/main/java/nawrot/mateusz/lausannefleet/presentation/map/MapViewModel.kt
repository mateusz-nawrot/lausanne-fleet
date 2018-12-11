package nawrot.mateusz.lausannefleet.presentation.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import nawrot.mateusz.lausannefleet.domain.car.AddCarUseCase
import nawrot.mateusz.lausannefleet.domain.car.CarAction
import nawrot.mateusz.lausannefleet.domain.station.GetStationsUseCase
import nawrot.mateusz.lausannefleet.presentation.base.BaseViewModel
import javax.inject.Inject


class MapViewModel @Inject constructor(private val getStationsUseCase: GetStationsUseCase,
                                       private val addCarUseCase: AddCarUseCase) : BaseViewModel() {


    fun init() {
        //check map availability - emit error VS if no play services installed
    }

    fun addCar(stationId: String): LiveData<CarAction> {
        val carData = MutableLiveData<CarAction>()
        manage(addCarUseCase.execute(stationId).subscribe(
                { carData.value = it },
                //TODO - handle error
                { }
        ))

        return carData
    }

}
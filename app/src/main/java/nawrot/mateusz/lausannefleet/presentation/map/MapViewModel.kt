package nawrot.mateusz.lausannefleet.presentation.map

import nawrot.mateusz.lausannefleet.domain.map.MapViewState
import nawrot.mateusz.lausannefleet.domain.station.GetStationsUseCase
import nawrot.mateusz.lausannefleet.presentation.base.BaseViewModel
import javax.inject.Inject


class MapViewModel @Inject constructor(private val getStationsUseCase: GetStationsUseCase) : BaseViewModel<MapViewState>() {

    override val defaultState = MapViewState()

    fun init() {
        //check map availability - emit error VS if no play services installed
    }

}
package nawrot.mateusz.lausannefleet.domain.station

import io.reactivex.Single
import nawrot.mateusz.lausannefleet.domain.SingleUseCase
import nawrot.mateusz.lausannefleet.domain.base.SchedulersProvider
import javax.inject.Inject


class GetCarStationsUseCase @Inject constructor(schedulersProvider: SchedulersProvider,
                                                private val stationRepository: StationRepository): SingleUseCase<Unit, List<Station>>(schedulersProvider) {

    override fun createUseCaseSingle(param: Unit): Single<List<Station>> {
        return stationRepository.getStations()
    }

}
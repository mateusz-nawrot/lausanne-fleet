package nawrot.mateusz.lausannefleet.domain.station

import io.reactivex.Single
import nawrot.mateusz.lausannefleet.domain.base.SingleUnitUseCase
import nawrot.mateusz.lausannefleet.domain.base.SchedulersProvider
import javax.inject.Inject


class GetStationsUseCase @Inject constructor(schedulersProvider: SchedulersProvider,
                                             private val stationRepository: StationRepository) : SingleUnitUseCase<List<Station>>(schedulersProvider) {

    override fun createUseCaseSingle(): Single<List<Station>> {
        return stationRepository.getStations()
    }

}
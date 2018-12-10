package nawrot.mateusz.lausannefleet.data.station

import io.reactivex.Single
import nawrot.mateusz.lausannefleet.domain.map.Position
import nawrot.mateusz.lausannefleet.domain.station.Station
import nawrot.mateusz.lausannefleet.domain.station.StationRepository
import javax.inject.Inject


class FleetStationRepository @Inject constructor() : StationRepository {

    override fun getStations(): Single<List<Station>> {
        TODO()
    }

    override fun getRandomStationPosition(excludedStationId: String): Single<Position> {
        TODO()
    }

}
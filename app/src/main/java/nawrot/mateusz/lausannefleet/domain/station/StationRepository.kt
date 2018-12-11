package nawrot.mateusz.lausannefleet.domain.station

import io.reactivex.Single
import nawrot.mateusz.lausannefleet.domain.map.Position


interface StationRepository {

    fun getStations(): Single<List<Station>>

    fun getTripOrigin(stationId: String): Single<Position>

    fun getTripDestination(startStationId: String): Single<Position>

}
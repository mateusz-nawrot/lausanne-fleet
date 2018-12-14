package nawrot.mateusz.lausannefleet.domain.station

import io.reactivex.Single


interface StationRepository {

    fun getStations(): Single<List<Station>>

    fun getTripOrigin(stationId: String): Single<Station>

    fun getTripDestination(stationId: String): Single<Station>

}
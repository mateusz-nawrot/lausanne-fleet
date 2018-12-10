package nawrot.mateusz.lausannefleet.domain.station

import io.reactivex.Single


interface StationRepository {

    fun getStations(): Single<List<Station>>
}
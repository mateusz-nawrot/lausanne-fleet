package nawrot.mateusz.lausannefleet.data.station

import io.reactivex.Single
import nawrot.mateusz.lausannefleet.domain.map.Position
import nawrot.mateusz.lausannefleet.domain.station.Station
import nawrot.mateusz.lausannefleet.domain.station.StationRepository
import javax.inject.Inject


class FleetStationRepository @Inject constructor() : StationRepository {

    companion object {
        const val STATION_SUFFIX = "station"
    }

    private val stations = listOf(
        Station("${STATION_SUFFIX}1", Position(46.517202, 6.629205)),
        Station("${STATION_SUFFIX}2", Position(46.541050, 6.658185)),
        Station("${STATION_SUFFIX}3", Position(46.541724, 6.618336)),
        Station("${STATION_SUFFIX}4", Position(46.522044, 6.565975)),
        Station("${STATION_SUFFIX}5", Position(46.511076, 6.659613))
    )

    override fun getStations(): Single<List<Station>> {
        return Single.just(stations)
    }

    override fun getTripOrigin(stationId: String): Single<Station> {
        //get random station from stations list excluding stationId
        val randomStationPosition = stations.filterNot { it.id == stationId }.shuffled().first()
        return Single.just(randomStationPosition)
    }

    override fun getTripDestination(stationId: String): Single<Station> {
        val startStation = stations.find { it.id == stationId }
        return if (startStation != null) {
            Single.just(startStation)
        } else {
            Single.error(IllegalStateException("Origin station can't be null"))
        }
    }

}
package nawrot.mateusz.lausannefleet.data.station

import io.reactivex.Single
import nawrot.mateusz.lausannefleet.domain.map.Position
import nawrot.mateusz.lausannefleet.domain.station.Station
import nawrot.mateusz.lausannefleet.domain.station.StationRepository
import javax.inject.Inject


class FleetStationRepository @Inject constructor() : StationRepository {

    private val stations = listOf(
        Station("1", Position(46.517202, 6.629205)),
        Station("2", Position(46.541050, 6.658185)),
        Station("3", Position(46.541724, 6.618336)),
        Station("4", Position(46.522044, 6.565975)),
        Station("5", Position(46.511076, 6.659613))
    )

    override fun getStations(): Single<List<Station>> {
        return Single.just(stations)
    }

    override fun getTripOrigin(stationId: String): Single<Position> {
        val startStation = stations.find { it.id == stationId }
        return if (startStation != null) {
            Single.just(startStation.position)
        } else {
            Single.error(IllegalStateException("Origin station can't be null"))
        }
    }

    //TODO error handling?
    override fun getTripDestination(startStationId: String): Single<Position> {
        //get random station from stations list excluding starting point
        val randomStationPosition = stations.filterNot { it.id == startStationId }.shuffled().first().position
        return Single.just(randomStationPosition)
    }

}
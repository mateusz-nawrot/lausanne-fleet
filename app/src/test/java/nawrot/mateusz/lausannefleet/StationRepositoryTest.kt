package nawrot.mateusz.lausannefleet

import nawrot.mateusz.lausannefleet.data.station.FleetStationRepository
import nawrot.mateusz.lausannefleet.domain.station.StationRepository
import org.junit.Before
import org.junit.Test


class StationRepositoryTest {

    private lateinit var stationRepository: StationRepository

    private val testStationId = "station1"

    @Before
    fun setup() {
        stationRepository = FleetStationRepository()
    }

    @Test
    fun `StationRepository returns proper destination station`() {
        //with current 'in memory' stations implementation we don't need to mock stations list API/database call

        val testObserver = stationRepository.getTripDestination(testStationId).test()

        testObserver.assertNoErrors()
        testObserver.assertComplete()
        testObserver.assertValue { station -> station.id == testStationId }
    }

    @Test
    fun `StationRepository returns proper origin station`() {
        val testObserver = stationRepository.getTripOrigin(testStationId).test()

        testObserver.assertNoErrors()
        testObserver.assertComplete()
        testObserver.assertValue { station -> station.id != testStationId }
    }
}